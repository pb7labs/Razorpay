package com.pb7technologies.razorpay.merchant.dto.request;

import com.pb7technologies.razorpay.common.enums.Environment;

public record CreateApiKeyRequest(
        Environment environment
) {

}
