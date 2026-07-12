package com.pb7technologies.razorpay.payment.statemachine;

import com.pb7technologies.razorpay.common.enums.PaymentActor;
import com.pb7technologies.razorpay.common.enums.PaymentEvent;
import com.pb7technologies.razorpay.common.enums.PaymentStatus;
import com.pb7technologies.razorpay.payment.entity.Payment;
import com.pb7technologies.razorpay.payment.entity.PaymentTransitionLog;
import com.pb7technologies.razorpay.payment.repository.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {

    private final PaymentTransitionLogRepository paymentTransitionLogRepository;
    private final PaymentStateMachine paymentStateMachine;

    public PaymentStatus apply(Payment payment, PaymentEvent event){
        PaymentStatus next = paymentStateMachine.transition(payment.getStatus(), event);
        PaymentTransitionLog log = PaymentTransitionLog.builder()
                .payment(payment)
                .fromStatus(payment.getStatus())
                .event(event)
                .toStatus(next)
                .actor(PaymentActor.SYSTEM)
                .occurredAt(LocalDateTime.now())
                .build();

        payment.setStatus(next);
        paymentTransitionLogRepository.save(log);
        return next;
    }
}
