package com.pb7technologies.razorpay.payment.gateway.adapters;

import com.pb7technologies.razorpay.payment.gateway.PaymentAdapter;
import com.pb7technologies.razorpay.payment.gateway.dto.PaymentRequest;
import com.pb7technologies.razorpay.payment.gateway.dto.PaymentResult;
import com.pb7technologies.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.pb7technologies.razorpay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CardPaymentAdapter implements PaymentAdapter {

    private final VaultService vaultService;

    @Override
    public PaymentResult initiate(PaymentRequest request) {
        String token = (String) request.methodDetails().get("token");

        PaymentProcessorResponse response = vaultService.charge(
                request.paymentId(), token, request.amount(), request.methodDetails()
        );

        return switch (response) {
            case PaymentProcessorResponse.Success success -> new PaymentResult.Success(success.bankReference());
            case PaymentProcessorResponse.Failure failure ->
                    new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
            case PaymentProcessorResponse.Pending pending -> new PaymentResult.Pending(pending.processorRef());
        };
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return null;
    }
}
