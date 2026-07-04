package com.pb7technologies.razorpay.merchant.entity;

import com.pb7technologies.razorpay.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "merchant_webhook_config",
    indexes = {
        @Index(name = "idx_webhook_merchant_id", columnList = "merchant_id, enabled"),
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MerchantWebhookConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(nullable = false, length = 255)
    private String targetUrl;

    @Column(length = 255)
    private String webhookSecretHash;

    // type of events which merchant wants to listen [orders.created, payment.captured]
    // Comma-separated event types
    @Column(length = 255)
    private String eventTypes;

    @Column(nullable = false)
    private Boolean enabled;
}
