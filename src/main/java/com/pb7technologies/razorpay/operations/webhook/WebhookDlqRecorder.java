package com.pb7technologies.razorpay.operations.webhook;

import com.pb7technologies.razorpay.common.enums.WebhookEventStatus;
import com.pb7technologies.razorpay.operations.entity.DlqEvent;
import com.pb7technologies.razorpay.operations.entity.WebhookEvent;
import com.pb7technologies.razorpay.operations.repository.DlqEventRepository;
import com.pb7technologies.razorpay.operations.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebhookDlqRecorder {

    private final WebhookEventRepository webhookEventRepository;
    private final DlqEventRepository dlqEventRepository;

    //previous function does not have transaction even then start new transaction
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordAfterAttemptExhausted(WebhookEvent webhookEvent, String finalError) {
        log.debug("Recording the, webhook event ID: {}", webhookEvent.getId());
        webhookEvent.setStatus((WebhookEventStatus.DEAD));
        webhookEventRepository.save(webhookEvent);

        DlqEvent dlqEvent = DlqEvent.builder()
                .webhookEvent(webhookEvent)
                .merchantId(webhookEvent.getMerchantId())
                .finalError(finalError)
                .payload(webhookEvent.getPayload())
                .build();

        dlqEventRepository.save(dlqEvent);
    }

    public void recordConsumerFailed(ConsumerRecord<String, Map<String, Object>> record, String error) {

        Map<String, Object> envelope = record.value();
        UUID merchantId = null;
        try {
            Map<String, Object> data = (Map<String, Object>) envelope.get("data");
            Object merchantIdRaw = data != null ? data.get("merchantId") : null;
            if (merchantIdRaw != null) {
                merchantId = UUID.fromString(merchantIdRaw.toString());
            }

        } catch (Exception ignored) {
            // if envelope is null don't do anything
        }
        log.debug("Recording the dlq because consumer failed, webhook merchant ID: {}", merchantId);
        DlqEvent event = DlqEvent.builder()
                .webhookEvent(null)
                .merchantId(merchantId)
                .finalError(error)
                .payload(envelope != null ? envelope : Map.of())
                .build();
    }
}
