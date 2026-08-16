package com.pb7technologies.razorpay.payment.service.impl;

import com.pb7technologies.razorpay.common.enums.PaymentStatus;
import com.pb7technologies.razorpay.payment.api.PaymentLookupService;
import com.pb7technologies.razorpay.payment.entity.Payment;
import com.pb7technologies.razorpay.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceLookupImpl implements PaymentLookupService {

    private final PaymentRepository paymentRepository;

    @Override
    public List<Payment> findUnsettledCapturedPayments(UUID merchantId) {
        return paymentRepository.findByMerchantIdAndStatusForUpdate(merchantId, PaymentStatus.CAPTURED);
    }
}
