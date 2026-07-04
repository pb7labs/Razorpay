package com.pb7technologies.razorpay.merchant.dto.request;

import com.pb7technologies.razorpay.common.enums.BusinessType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MerchantSignUpRequest(
        @NotNull(message = "Name is required")
        @Size(max = 50, message = "Name should not be more than 50 characters long")
        String name,

        @Email
        @NotNull(message = "Email is required")
        String email,

        @NotNull(message = "Password is required")
        @Size(min = 8, message = "Password should be 8 Characters long")
        String password,

        @Size(max = 50, message = "Business name should not be more than 50 characters")
        String businessName,

        BusinessType businessType
) {
}
