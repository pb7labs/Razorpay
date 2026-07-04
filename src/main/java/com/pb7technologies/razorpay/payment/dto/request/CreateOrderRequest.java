package com.pb7technologies.razorpay.payment.dto.request;

import com.pb7technologies.razorpay.common.entity.Money;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Map;

public record CreateOrderRequest(
        @NotNull
        Money amount,
        @Size(max = 100)
        String receipt, // order id known to merchant

        Map<String, Object> notes,

        LocalDateTime expiresAt // accept payment until this time
) {
}
