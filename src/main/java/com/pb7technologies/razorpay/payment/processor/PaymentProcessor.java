package com.pb7technologies.razorpay.payment.processor;

import com.pb7technologies.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.pb7technologies.razorpay.payment.processor.dto.PaymentProcessorResponse;

public interface PaymentProcessor {

    PaymentProcessorResponse charge(PaymentProcessorRequest request);
}
