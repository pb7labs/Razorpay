package com.pb7technologies.razorpay.merchant.service;

import com.pb7technologies.razorpay.merchant.dto.request.MerchantSignUpRequest;
import com.pb7technologies.razorpay.merchant.dto.response.MerchantResponse;

public interface AuthService {
    MerchantResponse signup(MerchantSignUpRequest request);
}
