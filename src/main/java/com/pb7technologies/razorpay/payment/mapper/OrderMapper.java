package com.pb7technologies.razorpay.payment.mapper;

import com.pb7technologies.razorpay.payment.dto.response.OrderResponse;
import com.pb7technologies.razorpay.payment.entity.OrderRecord;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    OrderResponse toResponse(OrderRecord orderRecord);
}
