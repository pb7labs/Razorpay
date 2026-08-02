package com.pb7technologies.razorpay.merchant.api;

import com.pb7technologies.razorpay.common.dto.WebhookTarget;

import java.util.List;
import java.util.UUID;

public interface MerchantWebhookApi {
    List<WebhookTarget> getActiveConfigsForEvent(UUID merchantId, String eventType);
}
