package com.pb7technologies.razorpay.payment.processor;

import com.pb7technologies.razorpay.common.enums.PaymentMethod;
import com.pb7technologies.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.pb7technologies.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentProcessorRouter {

    private final Map<PaymentMethod, PaymentProcessor> paymentProcessors;
    public PaymentProcessorResponse charge(PaymentProcessorRequest request){
        PaymentProcessor paymentProcessor = paymentProcessors.get(request.method());
        if(paymentProcessor == null){
            throw new IllegalArgumentException("No payment processor registered for this method: " + request.method());
        }
        return paymentProcessor.charge(request);
    }
}
