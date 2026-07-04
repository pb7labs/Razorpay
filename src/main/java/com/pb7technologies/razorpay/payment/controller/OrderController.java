package com.pb7technologies.razorpay.payment.controller;

import com.pb7technologies.razorpay.payment.dto.request.CreateOrderRequest;
import com.pb7technologies.razorpay.payment.dto.response.OrderResponse;
import com.pb7technologies.razorpay.payment.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    UUID merchantID = UUID.fromString("8d0bb0d1-b6e6-4538-aa06-5672bfb94e5b"); //TODO replace with MerchantContext

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid CreateOrderRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.create(merchantID,request));
    }
}
