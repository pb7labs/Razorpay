package com.pb7technologies.razorpay.payment.config;

import com.pb7technologies.razorpay.common.enums.PaymentMethod;
import com.pb7technologies.razorpay.payment.gateway.PaymentAdapter;
import com.pb7technologies.razorpay.payment.gateway.adapters.CardPaymentAdapter;
import com.pb7technologies.razorpay.payment.gateway.adapters.NetBankingAdapter;
import com.pb7technologies.razorpay.payment.gateway.adapters.UpiPaymentAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentAdapterConfig {

    @Bean
    public Map<PaymentMethod, PaymentAdapter> paymentAdapterMap() {
        return Map.of(
                PaymentMethod.CARD, new CardPaymentAdapter(),
                PaymentMethod.NETBANKING, new NetBankingAdapter(),
                PaymentMethod.UPI, new UpiPaymentAdapter()
        );
    }
}
