package com.pb7technologies.razorpay.operations.settlement;

import com.pb7technologies.razorpay.common.entity.Money;
import com.pb7technologies.razorpay.operations.settlement.dto.BankTransferResult;

import java.util.UUID;

public interface BankTransferProcessor {

    BankTransferResult initiate(UUID settlementId, UUID merchantId, Money amount, String bankAccount, String ifsc);
}
