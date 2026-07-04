package com.pb7technologies.razorpay.payment.repository;

import com.pb7technologies.razorpay.payment.dto.response.OrderResponse;
import com.pb7technologies.razorpay.payment.entity.OrderRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderRecord, UUID> {
    boolean existsByMerchantIdAndReceipt(UUID merchantID, String receipt);

    Optional<OrderRecord> findByIdAndMerchantId(UUID merchantId, UUID orderId);
}
