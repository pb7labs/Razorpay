package com.pb7technologies.razorpay.payment.gateway;

import com.pb7technologies.razorpay.payment.gateway.dto.PaymentRequest;
import com.pb7technologies.razorpay.payment.gateway.dto.PaymentResult;

public interface PaymentAdapter {

    PaymentResult initiate(PaymentRequest request);
}
