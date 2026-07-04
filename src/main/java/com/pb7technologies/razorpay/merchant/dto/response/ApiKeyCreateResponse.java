package com.pb7technologies.razorpay.merchant.dto.response;

import com.pb7technologies.razorpay.common.enums.Environment;

import java.util.UUID;

public record ApiKeyCreateResponse(
        UUID id,
        String keyId,
        String keySecret,
        Environment environment
) {
}
