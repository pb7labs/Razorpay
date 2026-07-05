package com.pb7technologies.razorpay.payment.config;

import com.pb7technologies.razorpay.common.enums.PaymentMethod;
import com.pb7technologies.razorpay.payment.gateway.PaymentAdapter;
import com.pb7technologies.razorpay.payment.gateway.adapters.CardPaymentAdapter;
import com.pb7technologies.razorpay.payment.gateway.adapters.NetBankingAdapter;
import com.pb7technologies.razorpay.payment.gateway.adapters.UpiPaymentAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentAdapterConfig {

    private final NetBankingAdapter netBankingAdapter;
    private final CardPaymentAdapter cardPaymentAdapter;
    private final UpiPaymentAdapter upiPaymentAdapter;

    @Bean
    public Map<PaymentMethod, PaymentAdapter> paymentAdapterMap() {
        return Map.of(
                PaymentMethod.CARD, cardPaymentAdapter,
                PaymentMethod.NETBANKING, netBankingAdapter,
                PaymentMethod.UPI, upiPaymentAdapter
        );
    }
}
