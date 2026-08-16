package com.pb7technologies.razorpay.operations.settlement;

import com.pb7technologies.razorpay.common.dto.SettlementBankDetails;
import com.pb7technologies.razorpay.common.entity.Money;
import com.pb7technologies.razorpay.common.enums.EventAggregateType;
import com.pb7technologies.razorpay.common.enums.SettlementStatus;
import com.pb7technologies.razorpay.common.exception.ResourceNotFoundException;
import com.pb7technologies.razorpay.merchant.api.MerchantLookupService;
import com.pb7technologies.razorpay.operations.entity.SettlePayment;
import com.pb7technologies.razorpay.operations.entity.Settlement;
import com.pb7technologies.razorpay.operations.entity.SettlementPaymentId;
import com.pb7technologies.razorpay.operations.repository.SettlementPaymentRepository;
import com.pb7technologies.razorpay.operations.repository.SettlementRepository;
import com.pb7technologies.razorpay.operations.settlement.dto.BankTransferResult;
import com.pb7technologies.razorpay.payment.api.PaymentLookupService;
import com.pb7technologies.razorpay.payment.entity.Payment;
import com.pb7technologies.razorpay.payment.outbox.OutboxEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class SettlementTransactionExecutor {

    private static final double FEE_RATE = 0.02;
    private static final double GST_RATE = 0.10;
    private final PaymentLookupService paymentLookupService;
    private final SettlementRepository settlementRepository;
    private final SettlementPaymentRepository settlementPaymentRepository;
    private final MerchantLookupService merchantLookupService;
    private final BankTransferProcessor bankTransferProcessor;
    //TODO: publisher insider its on DB
    private final OutboxEventPublisher outboxEventPublisher;

    public void processForMerchant(UUID merchantId, LocalDateTime settlementDate) {
        List<Payment> unsettledPayment = paymentLookupService.findUnsettledCapturedPayments(merchantId);
        if (unsettledPayment.isEmpty()) return;

        log.info("Processing {} usettled payments for merchantId: {} on {} date", unsettledPayment.size(), merchantId, settlementDate);

        Money gross = unsettledPayment.stream()
                .map(Payment::getAmount)
                .reduce(Money::add)// adding all money objects into 1 Money object
                .orElseThrow();

        int fee = Math.toIntExact(Math.round(gross.getAmountUnits() * FEE_RATE));
        int gst = Math.toIntExact(Math.round(fee * GST_RATE));
        Money feeAmount = Money.of(fee, gross.getCurrency());
        Money gstAmount = Money.of(gst, gross.getCurrency());
        Money netAmount = gross.subtract(feeAmount).subtract(gstAmount);

        Settlement settlement = Settlement.builder()
                .merchantId(merchantId)
                .grossAmount(gross)
                .feeAmount(feeAmount)
                .gstAmount(gstAmount)
                .netAmount(netAmount)
                .status(SettlementStatus.INITIATED)
                .build();

        settlementRepository.save(settlement);

        try {
            List<SettlePayment> links = new ArrayList<>();
            for (Payment p : unsettledPayment) {
                links.add(SettlePayment.builder()
                        .id(new SettlementPaymentId(settlement.getId(), p.getId()))
                        .settlement(settlement)
                        .build());
            }

            settlementPaymentRepository.saveAll(links);

            SettlementBankDetails settlementBankDetails = merchantLookupService.getSettlementBankDetails(merchantId);

            BankTransferResult bankTransferResult = bankTransferProcessor.initiate(
                    settlement.getId(),
                    merchantId,
                    netAmount,
                    settlementBankDetails.accountNumber(),
                    settlementBankDetails.ifsc()
            );

            settlement.setStatus(SettlementStatus.TRANSFER_PENDING);
            settlement.setBankReference(bankTransferResult.registrationRef());

            settlementRepository.save(settlement);
        } catch (Exception e) {
            log.error("Settlement failed for settlementId: {} on date: {}", settlement.getId(), settlementDate);
            settlement.setStatus(SettlementStatus.FAILED);
            settlementRepository.save(settlement);
        }
    }

    public void resolveTransfer(UUID settlementId,
                                String errorCode, String errorDescription) {

        Settlement settlement = settlementRepository.findById(settlementId).orElseThrow(
                () -> new ResourceNotFoundException("Settlement not for id : {}", settlementId)
        );
        if (settlement.getStatus() != SettlementStatus.TRANSFER_PENDING) {
            log.info("Settlement resolved, skipping for id: {}", settlement.getId());
            return;
        }

        if (errorCode != null) { //success
            settlement.setStatus(SettlementStatus.PROCESSED);
            settlement.setProcessedAt(LocalDateTime.now());
            settlementRepository.save(settlement);
            log.info("Settlement processed successfully, settlement ID: {}", settlementId);
            outboxEventPublisher.publish(
                    EventAggregateType.SETTLEMENT,
                    settlementId,
                    "SETTLEMENT_PROCESSED",
                    Map.of(
                            "settlementId", settlement,
                            "merchantId", settlement.getMerchantId(),
                            "status", settlement.getStatus().name(),
                            "settlementAmount", settlement.getNetAmount().getAmountUnits(),
                            "settlementCurrency", settlement.getNetAmount().getCurrency()
                    )
            );
        } else { //failed
            settlement.setStatus(SettlementStatus.FAILED);
            settlement.setFailureReason(errorCode + " : " + errorDescription);
            settlementRepository.save(settlement);
            log.warn("Settlement failed settlement ID: {}", settlementId);
            outboxEventPublisher.publish(
                    EventAggregateType.SETTLEMENT,
                    settlementId,
                    "SETTLEMENT_FAILED",
                    Map.of(
                            "settlementId", settlement,
                            "merchantId", settlement.getMerchantId(),
                            "status", settlement.getStatus().name(),
                            "settlementAmount", settlement.getNetAmount().getAmountUnits(),
                            "settlementCurrency", settlement.getNetAmount().getCurrency()
                    )
            );
        }
    }

}
