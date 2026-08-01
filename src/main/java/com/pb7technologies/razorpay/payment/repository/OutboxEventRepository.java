package com.pb7technologies.razorpay.payment.repository;

import com.pb7technologies.razorpay.common.enums.OutboxStatus;
import com.pb7technologies.razorpay.payment.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
