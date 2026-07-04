package com.pb7technologies.razorpay.payment.service;

import com.pb7technologies.razorpay.payment.dto.request.CreateOrderRequest;
import com.pb7technologies.razorpay.payment.dto.response.OrderResponse;
import com.pb7technologies.razorpay.payment.dto.response.PaymentResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse create(UUID merchantID, CreateOrderRequest request);

    OrderResponse getById(UUID merchantId, UUID orderId);

    OrderResponse cancel(UUID merchantId, UUID orderId);

    List<PaymentResponse> listPayments(UUID merchantId, UUID orderId);
}
