package com.pb7technologies.razorpay.merchant.mapper;

import com.pb7technologies.razorpay.merchant.dto.request.MerchantSignUpRequest;
import com.pb7technologies.razorpay.merchant.dto.response.MerchantResponse;
import com.pb7technologies.razorpay.merchant.entity.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MerchantMapper {

    Merchant toEntityFromSignupRequest(MerchantSignUpRequest merchantSignUpRequest);

    MerchantResponse toResponse(Merchant merchant);
}
