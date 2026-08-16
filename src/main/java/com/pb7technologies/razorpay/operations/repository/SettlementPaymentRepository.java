package com.pb7technologies.razorpay.operations.repository;

import com.pb7technologies.razorpay.operations.entity.SettlePayment;
import com.pb7technologies.razorpay.operations.entity.SettlementPaymentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementPaymentRepository extends JpaRepository<SettlePayment, SettlementPaymentId> {
}
