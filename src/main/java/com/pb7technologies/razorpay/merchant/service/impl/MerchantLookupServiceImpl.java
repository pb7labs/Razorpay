package com.pb7technologies.razorpay.merchant.service.impl;

import com.pb7technologies.razorpay.common.dto.SettlementBankDetails;
import com.pb7technologies.razorpay.common.dto.WebhookTarget;
import com.pb7technologies.razorpay.common.enums.MerchantStatus;
import com.pb7technologies.razorpay.common.exception.ResourceNotFoundException;
import com.pb7technologies.razorpay.merchant.api.MerchantLookupService;
import com.pb7technologies.razorpay.merchant.entity.Merchant;
import com.pb7technologies.razorpay.merchant.repository.MerchantRepository;
import com.pb7technologies.razorpay.merchant.repository.WebhookConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MerchantLookupServiceImpl implements MerchantLookupService {

    private final MerchantRepository merchantRepository;
    private final WebhookConfigRepository merchantWebhookConfigRepository;
    private final BytesEncryptor bytesEncryptor;


    @Override
    public List<WebhookTarget> getActiveConfigsForEvent(UUID merchantId, String eventType) {
        return merchantWebhookConfigRepository.findByMerchant_IdAndEnabledTrue(merchantId).stream()
                .filter(config -> config.isSubscribedTo(eventType))
                .map(config -> {
                    byte[] encryptedSecretBytes = Base64.getDecoder().decode(config.getWebhookSecret());
                    byte[] decryptSecretBytes = bytesEncryptor.decrypt(encryptedSecretBytes);
                    return new WebhookTarget(
                            config.getId(),
                            config.getTargetUrl(),
                            new String(decryptSecretBytes, StandardCharsets.UTF_8));
                }).toList();
    }

    @Override
    public List<UUID> listActiveMerchantIds() {
        return merchantRepository.findByStatus(MerchantStatus.ACTIVE)
                .stream().map(merchant -> merchant.getId()).toList();
    }

    @Override
    public SettlementBankDetails getSettlementBankDetails(UUID merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", merchantId));

        return new SettlementBankDetails(
                merchant.getSettlementBankAccount(),
                merchant.getSettlementBankIfsc(),
                merchant.getSettlementBankAccountHolderName()
        );
    }
}
