package com.pb7technologies.razorpay.payment.dto.request;

import com.pb7technologies.razorpay.common.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;


public record PaymentInitRequest(
        @NotNull(message = "Order ID is required")
        UUID orderId,
        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,
        Map<String, Object> methodDetails
) {
}
