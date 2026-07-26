package com.pb7technologies.razorpay.payment.service.impl;

import com.pb7technologies.razorpay.common.enums.OrderStatus;
import com.pb7technologies.razorpay.common.exception.BusinessRuleViolationException;
import com.pb7technologies.razorpay.common.exception.DuplicateResourceException;
import com.pb7technologies.razorpay.common.exception.ResourceNotFoundException;
import com.pb7technologies.razorpay.merchant.service.CustomerService;
import com.pb7technologies.razorpay.payment.dto.request.CreateOrderRequest;
import com.pb7technologies.razorpay.payment.dto.response.OrderResponse;
import com.pb7technologies.razorpay.payment.dto.response.PaymentResponse;
import com.pb7technologies.razorpay.payment.entity.OrderRecord;
import com.pb7technologies.razorpay.payment.entity.Payment;
import com.pb7technologies.razorpay.payment.mapper.OrderMapper;
import com.pb7technologies.razorpay.payment.mapper.PaymentMapper;
import com.pb7technologies.razorpay.payment.repository.OrderRepository;
import com.pb7technologies.razorpay.payment.repository.PaymentRepository;
import com.pb7technologies.razorpay.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;
    private final CustomerService customerService;

    @Value("${payment.order.default-order-expiry-minutes:30}")
    private int defaultExpiryMinutes;

    @Override
    @Transactional
    public OrderResponse create(UUID merchantID, CreateOrderRequest request) {
        if (request.receipt() != null && orderRepository.existsByMerchantIdAndReceipt(merchantID, request.receipt())) {
            throw new DuplicateResourceException("ORDER_RECEIPT_DUPLICATE", "Order with receipt already exists: " + request.receipt());
        }

        UUID customerId = null;
        if (request.customer() != null) {
            customerId = customerService.findOrCreate(
                    merchantID,
                    request.customer().email(),
                    request.customer().name(),
                    request.customer().phone()
            );
        }

        OrderRecord order = OrderRecord.builder()
                .receipt(request.receipt())
                .amount(request.amount())
                .notes(request.notes())
                .customerId(customerId)
                .merchantId(merchantID)
                .orderStatus(OrderStatus.CREATED)
                .expiresAt(request.expiresAt() != null ? request.expiresAt()
                        : LocalDateTime.now().plusMinutes(defaultExpiryMinutes))
                .build();

        order = orderRepository.save(order);

        //TODO Publish Kafka events about order creation

        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse getById(UUID merchantId, UUID orderId) {
        OrderRecord orderRecord = orderRepository.findByIdAndMerchantId(merchantId, orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        return orderMapper.toResponse(orderRecord);
    }

    @Override
    @Transactional
    public OrderResponse cancel(UUID merchantId, UUID orderId) {
        OrderRecord orderRecord = orderRepository.findByIdAndMerchantId(merchantId, orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        if (orderRecord.getOrderStatus() == OrderStatus.CANCELLED || orderRecord.getOrderStatus() == OrderStatus.PAID) {
            throw new BusinessRuleViolationException("ORDER_CANNOT_CANCEL",
                    "Cannot cancel order with status: " + orderRecord.getOrderStatus().name());
        }
        orderRecord.setOrderStatus(OrderStatus.CANCELLED);
        orderRecord = orderRepository.save(orderRecord);
        return orderMapper.toResponse(orderRecord);
    }

    @Override
    public List<PaymentResponse> listPayments(UUID merchantId, UUID orderId) {
        orderRepository.findByIdAndMerchantId(merchantId, orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        List<Payment> paymentList = paymentRepository.findByOrder_id(orderId);

        return paymentMapper.toResponseList(paymentList);
    }
}
