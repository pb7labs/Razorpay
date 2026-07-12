package com.pb7technologies.razorpay.merchant.service;

import com.pb7technologies.razorpay.merchant.dto.request.LoginRequest;
import com.pb7technologies.razorpay.merchant.dto.request.MerchantSignUpRequest;
import com.pb7technologies.razorpay.merchant.dto.response.LoginResponse;
import com.pb7technologies.razorpay.merchant.dto.response.MerchantResponse;
import jakarta.validation.Valid;

public interface AuthService {
    MerchantResponse signup(MerchantSignUpRequest request);

    LoginResponse login(LoginRequest request);
}
