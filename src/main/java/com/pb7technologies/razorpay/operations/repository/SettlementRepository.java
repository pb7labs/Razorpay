package com.pb7technologies.razorpay.operations.repository;

import com.pb7technologies.razorpay.common.enums.SettlementStatus;
import com.pb7technologies.razorpay.operations.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SettlementRepository extends JpaRepository<Settlement, UUID> {
    List<Settlement> findByStatus(SettlementStatus settlementStatus);
}
