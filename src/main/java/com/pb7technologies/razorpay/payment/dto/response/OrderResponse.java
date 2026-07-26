package com.pb7technologies.razorpay.payment.dto.response;

import com.pb7technologies.razorpay.common.entity.Money;
import com.pb7technologies.razorpay.common.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID merchantId,
        UUID customerId,
        String receipt,
        Money amount,
        OrderStatus status,
        int attempts,
        Map<String, Object> notes,
        LocalDateTime expiresAt,
        LocalDateTime createdAt
) {
}
