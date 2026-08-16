package com.pb7technologies.razorpay.operations.webhook;

import com.pb7technologies.razorpay.common.dto.WebhookTarget;
import com.pb7technologies.razorpay.common.enums.WebhookEventStatus;
import com.pb7technologies.razorpay.common.util.SignerUtil;
import com.pb7technologies.razorpay.merchant.api.MerchantLookupService;
import com.pb7technologies.razorpay.operations.entity.WebhookEvent;
import com.pb7technologies.razorpay.operations.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookKafkaConsumer {

    private final MerchantLookupService merchantLookupService;
    private final ObjectMapper objectMapper;
    private final SignerUtil signerUtil;
    private final WebhookEventRepository webhookEventRepository;
    private final WebhookRetryQueue retryQueue;
    private final WebhookDlqRecorder dlqRecorder;

    @KafkaListener(topics = {
            "${app.kafka.topics.payments:payments.events}",
            "${app.kafka.topics.orders:orders.events}",
            "${app.kafka.topics.refunds:refunds.events}",
            "${app.kafka.topics.settlements:settlements.events}"
    })
    public void onWebhookEvent(ConsumerRecord<String, Map<String, Object>> record, Acknowledgment ack) {
        Map<String, Object> envelope = record.value();
        Map<String, Object> data = (Map<String, Object>) envelope.get("data");
        String eventType = (String) envelope.get("eventType");

        Object merchantIdObject = data.get("merchantId");

        if (merchantIdObject == null) {
            log.warn("No MerchantId was found, skipping the event = {}", eventType);
            ack.acknowledge();
            return;
        }

        UUID merchantId = UUID.fromString(merchantIdObject.toString());

        List<WebhookTarget> targets = merchantLookupService.getActiveConfigsForEvent(
                merchantId,
                eventType
        );

        if (targets.isEmpty()) {
            log.debug("No webhook target was found, skipping the event = {}", eventType);
            ack.acknowledge();
            return;
        }

        try {
            Map<String, Object> signatureData = Map.of(
                    "event", eventType,
                    "payload", data
            );

            String signatureJson = objectMapper.writeValueAsString(signatureData);

            for (WebhookTarget target : targets) {
                String signature = signerUtil.sign(signatureJson, target.webhookSecret()); // sign the payload with secret
                WebhookEvent webhookEvent = WebhookEvent.builder()
                        .merchantId(merchantId)
                        .eventType(eventType)
                        .payload(data)
                        .targetUrl(target.targetUrl())
                        .signature(signature)
                        .status(WebhookEventStatus.PENDING)
                        .nextRetryAt(LocalDateTime.now())
                        .build();

                webhookEvent = webhookEventRepository.save(webhookEvent);

                // storing it to redis because we do not want to block threads by making call and leveraging the
                // work to the poller by inserting the events to the redis, and by this we make sure the database
                // do not get struck

                retryQueue.enqueue(webhookEvent.getId(), webhookEvent.getNextRetryAt());
                log.info("Created a Webhook event with id: {}", webhookEvent.getId());
            }
            ack.acknowledge();
        } catch (DataAccessException | CannotCreateTransactionException dbDown) {
            log.error("Webhook consumer failed due to DB down, could not process the record, offset: {}", record.offset());
        }catch (Exception logicError){
            dlqRecorder.recordConsumerFailed(record, logicError.getMessage());
            ack.acknowledge();
        }
    }
}
