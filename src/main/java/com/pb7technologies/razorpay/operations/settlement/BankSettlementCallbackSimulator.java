package com.pb7technologies.razorpay.operations.settlement;

import com.pb7technologies.razorpay.common.enums.SettlementStatus;
import com.pb7technologies.razorpay.common.util.RandomizerUtil;
import com.pb7technologies.razorpay.operations.entity.Settlement;
import com.pb7technologies.razorpay.operations.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankSettlementCallbackSimulator {

    private final SettlementRepository settlementRepository;
    private final SettlementTransactionExecutor settlementTransactionExecutor;

    @Scheduled(fixedDelayString = "5000")
    public void processCallback(){
        List<Settlement> settlements = settlementRepository.findByStatus(SettlementStatus.TRANSFER_PENDING);
        if(settlements.isEmpty()) return;

        for (Settlement settlement: settlements){
            simulateCallback(settlement);
        }
    }

    private void simulateCallback(Settlement settlement){
        log.info("Initiating settlement callback for settlement ID: {}", settlement.getId());
        String utrNumber = "UTR_" + RandomizerUtil.randomBase64(12);
        settlementTransactionExecutor.resolveTransfer(settlement.getId(), null, null);
    }
}
