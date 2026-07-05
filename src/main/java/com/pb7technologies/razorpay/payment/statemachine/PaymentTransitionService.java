package com.pb7technologies.razorpay.payment.statemachine;

import com.pb7technologies.razorpay.common.enums.PaymentActor;
import com.pb7technologies.razorpay.common.enums.PaymentEvent;
import com.pb7technologies.razorpay.common.enums.PaymentStatus;
import com.pb7technologies.razorpay.payment.entity.Payment;
import com.pb7technologies.razorpay.payment.entity.PaymentTransitionLog;
import com.pb7technologies.razorpay.payment.repository.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {

    private final PaymentTransitionLogRepository paymentTransitionLogRepository;
    private final PaymentStateMachine paymentStateMachine;

    public PaymentStatus apply(Payment payment, PaymentEvent event){
        PaymentStatus next = paymentStateMachine.transition(payment.getStatus(), event);
        payment.setStatus(next);
        PaymentTransitionLog log = PaymentTransitionLog.builder()
                .payment(payment)
                .fromStatus(payment.getStatus())
                .event(event)
                .toStatus(next)
                .actor(PaymentActor.SYSTEM)
                .build();

        paymentTransitionLogRepository.save(log);
        return next;
    }
}
