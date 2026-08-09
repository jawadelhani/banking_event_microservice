package com.example.aiservice.services;

import com.example.aiservice.entities.TransactionHistory;
import com.example.aiservice.events.FraudAnalysisEvent;
import com.example.aiservice.events.TransactionCreatedEvent;
import com.example.aiservice.repositories.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudScoringService {

    private final TransactionHistoryRepository repository;

    public FraudAnalysisEvent analyze(TransactionCreatedEvent event) {
        List<TransactionHistory> history = repository.findByClientId(event.getClientId());
        
        // Exclude current transaction from baseline stats
        List<TransactionHistory> baseline = history.stream()
                .filter(tx -> !tx.getTransactionId().equals(event.getTransactionId()))
                .toList();

        if (baseline.size() < 10) {
            // Not enough data for stable standard deviation
            return createFraudEvent(event, 0.0, false, "Not enough history (minimum 10 transactions) to calculate Z-Score.");
        }

        double sum = 0.0;
        for (TransactionHistory tx : baseline) {
            sum += Math.abs(tx.getAmount().doubleValue());
        }
        double mean = sum / baseline.size();

        double varianceSum = 0.0;
        for (TransactionHistory tx : baseline) {
            double diff = Math.abs(tx.getAmount().doubleValue()) - mean;
            varianceSum += diff * diff;
        }
        double stdDev = Math.sqrt(varianceSum / (baseline.size() - 1));

        // Prevent divide by zero if all previous amounts were identical
        stdDev = Math.max(stdDev, 1.0);

        double currentAmount = Math.abs(event.getAmount().doubleValue());
        double zScore = (currentAmount - mean) / stdDev;
        
        if (zScore > 3.0) {
            return createFraudEvent(event, 95.0, true, "Transaction amount is unusually high for this client (Z-Score: " + String.format("%.2f", zScore) + ").");
        }

        return createFraudEvent(event, 10.0, false, "Normal transaction (Z-Score: " + String.format("%.2f", zScore) + ").");
    }

    private FraudAnalysisEvent createFraudEvent(TransactionCreatedEvent event, double anomalyScore, boolean suspicious, String reason) {
        return FraudAnalysisEvent.builder()
                .transactionId(event.getTransactionId())
                .accountId(event.getAccountId())
                .clientId(event.getClientId())
                .accountNumber(event.getAccountNumber())
                .amount(event.getAmount())
                .type(event.getType())
                .createdAt(event.getCreatedAt())
                .anomalyScore(anomalyScore)
                .suspicious(suspicious)
                .reason(reason)
                .build();
    }
}
