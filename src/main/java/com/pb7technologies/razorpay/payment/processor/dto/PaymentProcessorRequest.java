package com.pb7technologies.razorpay.payment.processor.dto;

import com.pb7technologies.razorpay.common.entity.Money;
import com.pb7technologies.razorpay.common.enums.PaymentMethod;

import java.util.Map;

public record PaymentProcessorRequest(
        PaymentMethod method,
        Money amount,
        Map<String, Object> methodDetails
) {


}
