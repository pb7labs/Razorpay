package com.pb7technologies.razorpay.merchant.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisApiKeyCache implements ApiKeyCache {

    private static final String PREFIX = "apikey:";
    private static final Duration TTL = Duration.ofMinutes(5);

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<ApiKeyCacheEntry> get(String keyId) {
        try {
            String json = stringRedisTemplate.opsForValue().get(PREFIX + keyId);
            if (null == json) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(json, ApiKeyCacheEntry.class));
        } catch (RuntimeException e) {
            log.warn("ApiKey: Cache read failed, KeyId: {}", keyId);
            return Optional.empty();
        }
    }

    @Override
    public void put(String keyId, ApiKeyCacheEntry apiKeyCacheEntry) {
        try {
            stringRedisTemplate.opsForValue().set(PREFIX + keyId,
                    objectMapper.writeValueAsString(apiKeyCacheEntry),
                    TTL
            );
        } catch (Exception e) {
            log.warn("ApiKey: Cache put failed, KeyId: {}", keyId);
        }
    }

    @Override
    public void evict(String keyId) {
        stringRedisTemplate.delete(PREFIX + keyId);
    }
}
