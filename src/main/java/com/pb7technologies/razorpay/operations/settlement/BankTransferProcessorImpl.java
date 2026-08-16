package com.pb7technologies.razorpay.operations.settlement;

import com.pb7technologies.razorpay.common.entity.Money;
import com.pb7technologies.razorpay.common.util.RandomizerUtil;
import com.pb7technologies.razorpay.operations.settlement.dto.BankTransferResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class BankTransferProcessorImpl implements BankTransferProcessor {
    @Override
    public BankTransferResult initiate(UUID settlementId, UUID merchantId, Money amount, String bankAccount, String ifsc) {
        //call the Bank API

        String registerationRef = "TXN_" + RandomizerUtil.randomBase64(12);

        log.debug("Bank Transfer call completed for settlement id: {}, registerationRef: {}",
                settlementId, registerationRef);

        return new BankTransferResult(registerationRef);
    }
}
