package com.pb7technologies.razorpay.payment.service;

import com.pb7technologies.razorpay.payment.dto.request.PaymentInitRequest;
import com.pb7technologies.razorpay.payment.dto.response.PaymentResponse;

import java.util.UUID;

public interface PaymentService {
    PaymentResponse initiate(UUID merchantId, PaymentInitRequest request);

    PaymentResponse capture(UUID merchantId, UUID paymentId);
}
