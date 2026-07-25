package com.pb7technologies.razorpay.common.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.rate-limit.method", havingValue = "sliding-lua")
public class SlidingWindowLuaLimiter implements RateLimiter {

    private static final DefaultRedisScript<List> RATE_LIMIT_SCRIPT = createRateLimitScript();

    private final StringRedisTemplate redis;

    @Override
    public RateLimitResult check(String key, int maxRequestAllowed, long windowSeconds) {

        String redisKey = "rateLimit:sliding:" + key;
        long nowMs = System.currentTimeMillis();

        List<?> result = redis.execute(
                RATE_LIMIT_SCRIPT,
                List.of(redisKey),
                String.valueOf(nowMs),
                String.valueOf(maxRequestAllowed),
                String.valueOf(windowSeconds),
                UUID.randomUUID().toString()
        );

        if (result == null || result.size() < 3) {
            return RateLimitResult.allowed(maxRequestAllowed);
        }

        boolean allowed = toLong(result.get(0)) == 1;
        int remaining = toInt(result.get(1));
        int retryAfterSeconds = toInt(result.get(2));

        if (allowed) {
            return RateLimitResult.allowed(remaining);
        }

        return RateLimitResult.denied(retryAfterSeconds);
    }

    private long toLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(value.toString());
    }

    private int toInt(Object value) {
        return Math.toIntExact(toLong(value));
    }

    private static DefaultRedisScript<List> createRateLimitScript() {
        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("scripts/sliding-window-rate-limiter.lua"));
        script.setResultType(List.class);
        return script;
    }
}
