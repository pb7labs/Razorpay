package com.pb7technologies.razorpay.merchant.dto.response;

import com.pb7technologies.razorpay.common.enums.BusinessType;
import com.pb7technologies.razorpay.common.enums.MerchantStatus;

import java.util.UUID;

public record MerchantResponse(
        UUID id,
        String name,
        String email,
        String businessName,
        BusinessType businessType,
        MerchantStatus merchantStatus
) {
}
