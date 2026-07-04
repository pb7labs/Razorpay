package com.pb7technologies.razorpay.merchant.service.impl;

import com.pb7technologies.razorpay.common.enums.MerchantStatus;
import com.pb7technologies.razorpay.common.enums.UserRole;
import com.pb7technologies.razorpay.common.exception.DuplicateResourceException;
import com.pb7technologies.razorpay.merchant.dto.request.MerchantSignUpRequest;
import com.pb7technologies.razorpay.merchant.dto.response.MerchantResponse;
import com.pb7technologies.razorpay.merchant.entity.AppUser;
import com.pb7technologies.razorpay.merchant.entity.Merchant;
import com.pb7technologies.razorpay.merchant.mapper.MerchantMapper;
import com.pb7technologies.razorpay.merchant.repository.AppUserRepository;
import com.pb7technologies.razorpay.merchant.repository.MerchantRepository;
import com.pb7technologies.razorpay.merchant.service.AuthService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

//Why using Implementation - because we want the code to be extensible and also provide loose coupling

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final MerchantRepository merchantRepository;
    private final MerchantMapper merchantMapper;


    @Override
    @Transactional
    public MerchantResponse signup(MerchantSignUpRequest request) {
        if (merchantRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("DUPLICATE_MERCHANT_EMAIL","Merchant with Email already exists: " + request.email());
        }
        Merchant merchant = merchantMapper.toEntityFromSignupRequest(request);
        merchant.setStatus(MerchantStatus.PENDING_KYC);

        merchant = merchantRepository.save(merchant);

        AppUser user = AppUser.builder()
                .merchant(merchant)
                .email(request.email())
                .passwordHash(request.password()) //TODO: Encrypt password Hash
                .role(UserRole.OWNER)
                .build();

        appUserRepository.save(user);

        return merchantMapper.toResponse(merchant);
    }
}
