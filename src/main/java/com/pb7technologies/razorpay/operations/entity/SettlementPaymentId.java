package com.pb7technologies.razorpay.operations.entity;

import com.pb7technologies.razorpay.common.entity.BaseEntity;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Embeddable
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SettlementPaymentId {

    private UUID settlementId;

    private UUID paymentId;
}
