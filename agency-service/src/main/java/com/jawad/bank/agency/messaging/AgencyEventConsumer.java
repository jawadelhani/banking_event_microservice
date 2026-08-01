package com.jawad.bank.agency.messaging;

import com.jawad.bank.agency.entities.AgencyAlert;
import com.jawad.bank.agency.entities.AlertType;
import com.jawad.bank.agency.entities.Criticality;
import com.jawad.bank.agency.events.CardSuggestionEvent;
import com.jawad.bank.agency.events.FraudAnalysisEvent;
import com.jawad.bank.agency.events.TransactionCreatedEvent;
import com.jawad.bank.agency.repositories.AgencyAlertRepository;
import com.jawad.bank.agency.repositories.AgencyTransactionRepository;
import com.jawad.bank.agency.entities.AgencyTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgencyEventConsumer {

    private final AgencyAlertRepository agencyAlertRepository;
    private final AgencyTransactionRepository agencyTransactionRepository;

    @KafkaListener(topics = "transaction-events", containerFactory = "transactionKafkaListenerContainerFactory")
    public void consumeTransaction(TransactionCreatedEvent event) {
        AgencyTransaction transaction = AgencyTransaction.builder()
                .transactionId(event.getTransactionId())
                .accountId(event.getAccountId())
                .clientId(event.getClientId())
                .accountNumber(event.getAccountNumber())
                .amount(event.getAmount())
                .type(event.getType())
                .createdAt(event.getCreatedAt())
                .build();
        agencyTransactionRepository.save(transaction);
        log.info("Saved transaction {} for client {}", event.getTransactionId(), event.getClientId());
    }

    @KafkaListener(topics = "card-suggestions", containerFactory = "cardSuggestionKafkaListenerContainerFactory")
    public void consumeCardSuggestion(CardSuggestionEvent event) {
        AgencyAlert alert = AgencyAlert.builder()
                .clientId(event.getClientId())
                .txId(event.getTransactionId())
                .type(AlertType.CARD_SUGGESTION)
                .criticality(Criticality.INFO)
                .message(event.getMessage())
                .seenByAgent(false)
                .build();

        agencyAlertRepository.save(alert);
        log.info("Saved card-suggestion alert for client {}", event.getClientId());
    }

    // Not receiving anything yet — ai-service doesn't publish FraudAnalysisEvent
    // until fraud scoring is wired in there. Ready to go once it is.
    @KafkaListener(topics = "fraud-analysis", containerFactory = "fraudAnalysisKafkaListenerContainerFactory")
    public void consumeFraudAnalysis(FraudAnalysisEvent event) {
        if (!event.isSuspicious()) {
            return; // only surface genuinely suspicious transactions to agents
        }

        AgencyAlert alert = AgencyAlert.builder()
                .clientId(event.getClientId())
                .txId(event.getTransactionId())
                .type(AlertType.FRAUD)
                .criticality(classify(event.getAnomalyScore()))
                .message(event.getReason())
                .seenByAgent(false)
                .build();

        agencyAlertRepository.save(alert);
        log.info("Saved fraud alert for client {} (score={})", event.getClientId(), event.getAnomalyScore());
    }

    private Criticality classify(double score) {
        if (score >= 70) return Criticality.URGENT;
        if (score >= 35) return Criticality.ATTENTION;
        return Criticality.INFO;
    }
}