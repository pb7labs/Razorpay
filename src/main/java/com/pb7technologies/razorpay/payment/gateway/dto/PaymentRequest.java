package com.pb7technologies.razorpay.payment.gateway.dto;

import com.pb7technologies.razorpay.common.entity.Money;
import com.pb7technologies.razorpay.common.enums.PaymentMethod;

import java.util.Map;
import java.util.UUID;

public record PaymentRequest(
        UUID paymentId,
        UUID orderId,
        UUID merchantId,
        Money amount,
        PaymentMethod method,
        Map<String,Object> methodDetails
) {
}
