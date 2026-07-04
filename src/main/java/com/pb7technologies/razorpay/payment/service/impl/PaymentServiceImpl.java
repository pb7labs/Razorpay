package com.pb7technologies.razorpay.payment.service.impl;

import com.pb7technologies.razorpay.common.enums.OrderStatus;
import com.pb7technologies.razorpay.common.enums.PaymentStatus;
import com.pb7technologies.razorpay.common.exception.BusinessRuleViolationException;
import com.pb7technologies.razorpay.common.exception.ResourceNotFoundException;
import com.pb7technologies.razorpay.payment.dto.request.PaymentInitRequest;
import com.pb7technologies.razorpay.payment.dto.response.PaymentResponse;
import com.pb7technologies.razorpay.payment.entity.OrderRecord;
import com.pb7technologies.razorpay.payment.entity.Payment;
import com.pb7technologies.razorpay.payment.gateway.PaymentGatewayRouter;
import com.pb7technologies.razorpay.payment.gateway.dto.PaymentRequest;
import com.pb7technologies.razorpay.payment.gateway.dto.PaymentResult;
import com.pb7technologies.razorpay.payment.mapper.PaymentMapper;
import com.pb7technologies.razorpay.payment.repository.OrderRepository;
import com.pb7technologies.razorpay.payment.repository.PaymentRepository;
import com.pb7technologies.razorpay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponse initiate(UUID merchantId, PaymentInitRequest request) {
        OrderRecord order = orderRepository.findByIdAndMerchantId(request.orderId(), merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", request.orderId()));

        if (order.getOrderStatus() != OrderStatus.CREATED && order.getOrderStatus() != OrderStatus.ATTEMPTED) {
            throw new BusinessRuleViolationException("ORDER_NOT_PAYABLE",
                    "order cannot accept payment in status: " + order.getOrderStatus());
        }

        order.setOrderStatus(OrderStatus.ATTEMPTED);
        order.setAttempts(order.getAttempts() + 1);
        Payment payment = Payment.builder()
                .order(order)
                .merchantId(merchantId)
                .amount(order.getAmount())
                .status(PaymentStatus.CREATED)
                .methodDetails(request.methodDetails())
                .build();

        payment = paymentRepository.save(payment);

        PaymentRequest paymentRequest = new PaymentRequest(
                payment.getId(),
                request.orderId(),
                merchantId,
                order.getAmount(),
                request.paymentMethod(),
                request.methodDetails()
        );
        PaymentResult result = paymentGatewayRouter.initiate(paymentRequest);

        switch (result) {
            case PaymentResult.Pending pending -> payment.setProcessorReference(pending.registrationRef());
            case PaymentResult.Failure failure -> {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }
        }
        payment = paymentRepository.save(payment);
        orderRepository.save(order);
        return paymentMapper.toResponse(payment);
    }
}
