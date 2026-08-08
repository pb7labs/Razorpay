package com.pb7technologies.razorpay.operations.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class DummyMerchantWebhookController {

    @PostMapping("/success")
    public ResponseEntity<Void> handleWebhookEventSuccess(@RequestBody Map<String, Object> requestBody) {
        return ResponseEntity.noContent().build();
    }
}
