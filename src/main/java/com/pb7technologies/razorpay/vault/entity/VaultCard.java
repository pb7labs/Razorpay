package com.pb7technologies.razorpay.vault.entity;

import com.pb7technologies.razorpay.common.entity.BaseEntity;
import com.pb7technologies.razorpay.common.enums.CardBrand;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vault_card")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class VaultCard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 4)
    private String lastFour;

    @Column(nullable = false, length = 6)
    private String bin; //First six digit of card

    @Column(nullable = false)
    private byte[] encryptedPan; // PAN is User's Card number we stored it encypted

    @Column(nullable = false)
    private byte[] encryptedDek; // encrypted dek which helps encrypting PAN

    @Column(nullable = false)
    private CardBrand brand;

    @Column(nullable = false)
    private String expiryMonth;

    @Column(nullable = false)
    private String expiryYear;

    @Column(nullable = false)
    private String cardHolderName;

    private LocalDateTime deletedAt;
}
