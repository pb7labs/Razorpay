package com.pb7technologies.razorpay.payment.outbox;

import com.pb7technologies.razorpay.common.enums.EventAggregateType;
import com.pb7technologies.razorpay.payment.entity.OutboxEvent;
import com.pb7technologies.razorpay.payment.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;

    public void publish(EventAggregateType aggregateType, UUID aggregatedId,
                        String eventType, Map<String, Object> payload) {

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregatedId)
                .eventType(eventType)
                .payload(payload)
                .build();
        outboxEventRepository.save(outboxEvent);
    }
}
