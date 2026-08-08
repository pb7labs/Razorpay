package com.pb7technologies.razorpay.operations.repository;

import com.pb7technologies.razorpay.operations.entity.DlqEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DlqEventRepository extends JpaRepository<DlqEvent, UUID> {
}
