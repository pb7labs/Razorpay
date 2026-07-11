package com.pb7technologies.razorpay.payment.repository;

import com.pb7technologies.razorpay.common.enums.PaymentStatus;
import com.pb7technologies.razorpay.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByOrder_id(UUID orderId);

    Optional<Payment> findByIdAndMerchantId(UUID merchantId, UUID paymentId);

    List<Payment> findByStatusAndCreatedAtBefore(PaymentStatus paymentStatus, LocalDateTime globalWindow);
}
