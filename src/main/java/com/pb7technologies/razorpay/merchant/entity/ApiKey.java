package com.pb7technologies.razorpay.merchant.entity;

import com.pb7technologies.razorpay.common.entity.BaseEntity;
import com.pb7technologies.razorpay.common.enums.Environment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "api_key",
        indexes = {
                @Index(name = "idx_api_key_merchant_env", columnList = "merchant_id, environment, enabled")
        })
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiKey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(nullable = false, length = 50, unique = true)
    private String keyId;

    @Column(nullable = false, length = 200)
    private String keySecretHash;

    @Column(length = 200)
    private String previousKeySecretHash;

    @Column(nullable = false, length = 10)
    private Environment environment;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    private LocalDateTime lastUsedAt;
    private LocalDateTime rotatedAt;
    private LocalDateTime gracePeriodExpiredAt;

    public boolean isInGracePeriod() {
        return gracePeriodExpiredAt != null && LocalDateTime.now().isBefore(gracePeriodExpiredAt);
    }
}
