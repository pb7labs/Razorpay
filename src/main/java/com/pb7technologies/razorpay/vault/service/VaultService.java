package com.pb7technologies.razorpay.vault.service;

import com.pb7technologies.razorpay.common.entity.Money;
import com.pb7technologies.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.pb7technologies.razorpay.vault.dto.request.TokenizeRequest;
import com.pb7technologies.razorpay.vault.dto.response.TokenizeResponse;
import jakarta.validation.Valid;

import java.util.Map;
import java.util.UUID;

public interface VaultService {
    TokenizeResponse tokenize(@Valid TokenizeRequest request, UUID merchantId);

    PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> methodDetails);
}
