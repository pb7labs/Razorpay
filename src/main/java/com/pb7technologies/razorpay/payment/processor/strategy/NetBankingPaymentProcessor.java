package com.pb7technologies.razorpay.payment.processor.strategy;

import com.pb7technologies.razorpay.payment.processor.PaymentProcessor;
import com.pb7technologies.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.pb7technologies.razorpay.payment.processor.dto.PaymentProcessorResponse;

public class NetBankingPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        return null;
    }
}
