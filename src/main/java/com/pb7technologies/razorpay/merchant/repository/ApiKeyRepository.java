package com.pb7technologies.razorpay.merchant.repository;

import com.pb7technologies.razorpay.merchant.dto.response.ApiKeyResponse;
import com.pb7technologies.razorpay.merchant.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    List<ApiKey> findByMerchant_id(UUID merchantId);

    Optional<ApiKey> findByKeyId(String keyId);
}
