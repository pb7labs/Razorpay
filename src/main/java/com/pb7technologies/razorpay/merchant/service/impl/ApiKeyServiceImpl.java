package com.pb7technologies.razorpay.merchant.service.impl;

import com.pb7technologies.razorpay.common.exception.ResourceNotFoundException;
import com.pb7technologies.razorpay.common.util.RandomizerUtil;
import com.pb7technologies.razorpay.merchant.cache.ApiKeyCache;
import com.pb7technologies.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.pb7technologies.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.pb7technologies.razorpay.merchant.dto.response.ApiKeyResponse;
import com.pb7technologies.razorpay.merchant.entity.ApiKey;
import com.pb7technologies.razorpay.merchant.entity.Merchant;
import com.pb7technologies.razorpay.merchant.mapper.ApiKeyMapper;
import com.pb7technologies.razorpay.merchant.repository.ApiKeyRepository;
import com.pb7technologies.razorpay.merchant.repository.MerchantRepository;
import com.pb7technologies.razorpay.merchant.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ApiKeyServiceImpl implements ApiKeyService {

    private final MerchantRepository merchantRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyMapper apiKeyMapper;
    private final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();
    private final ApiKeyCache apiKeyCache;

    @Override
    @Transactional
    public ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("merchant", merchantId.toString()));

        String keyId = "rzp_" + request.environment().name().toLowerCase() + "_" + RandomizerUtil.randomBase64(24);
        String rawSecret = RandomizerUtil.randomBase64(40);

        ApiKey apiKey = ApiKey.builder()
                .merchant(merchant)
                .keyId(keyId)
                .environment(request.environment())
                .keySecretHash(BCRYPT.encode(rawSecret))
                .build();

        apiKey = apiKeyRepository.save(apiKey);
        return new ApiKeyCreateResponse(
                apiKey.getId(),
                keyId,
                rawSecret,
                request.environment()
        );
    }

    @Override
    public List<ApiKeyResponse> listByMerchant(UUID merchantId) {
        return apiKeyMapper.toResponseList(apiKeyRepository.findByMerchant_id(merchantId));
    }

    @Override
    @Transactional
    public void revoke(UUID merchantId, UUID keyId) {
        ApiKey key = apiKeyRepository.findById(keyId)
                .filter(k -> k.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("ApiKey", keyId));

        key.setEnabled(false);
        apiKeyCache.evict(key.getKeyId());
    }

    @Override
    @Transactional
    public ApiKeyCreateResponse rotate(UUID merchantId, UUID keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .filter(k -> k.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("ApiKey", keyId));
        if (!apiKey.isEnabled()) {
            throw new RuntimeException("Cannot rotate disabled key");
        }
        String newRawSecret = RandomizerUtil.randomBase64(40);
        apiKey.setPreviousKeySecretHash(apiKey.getKeySecretHash());
        apiKey.setKeySecretHash(BCRYPT.encode(newRawSecret));
        apiKey.setRotatedAt(LocalDateTime.now());
        apiKey.setGracePeriodExpiredAt(LocalDateTime.now().plusHours(24));
        apiKey = apiKeyRepository.save(apiKey);

        apiKeyCache.evict(apiKey.getKeyId());
        return new ApiKeyCreateResponse(
                apiKey.getId(),
                apiKey.getKeyId(),
                newRawSecret,
                apiKey.getEnvironment()
        );
    }
}
