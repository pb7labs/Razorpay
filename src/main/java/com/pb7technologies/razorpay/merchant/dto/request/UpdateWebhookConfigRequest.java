package com.pb7technologies.razorpay.merchant.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateWebhookConfigRequest(
        @NotBlank(message = "Webhook url is required")
        @Size(max = 500)
        @Pattern(regexp = "^https?://.+", message = "Webhook url must be valid http(s) URL")
        String targetUrl,

        // Comma separated event types (e.g "PAYMENT_STATUS_CHANGES, REFUND_CREATED")
        //Null,blank,all subscribe to every event type
        @Size(max = 1000)
        String eventTypes
) {
}
