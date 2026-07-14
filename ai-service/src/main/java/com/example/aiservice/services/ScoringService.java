package com.example.aiservice.services;


import com.example.aiservice.dtos.GroqVerdict;
import com.example.aiservice.groq.GroqClient;
import com.example.aiservice.messaging.FraudAnalysisProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.aiservice.entities.TransactionHistory;
import com.example.aiservice.events.FraudAnalysisEvent;
import com.example.aiservice.events.TransactionCreatedEvent;
import com.example.aiservice.repositories.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoringService {

    private final TransactionHistoryRepository historyRepository;
    private final GroqClient groqClient;
    private final FraudAnalysisProducer producer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void handle(TransactionCreatedEvent event) {

        TransactionHistory history = TransactionHistory.builder()
                .transactionId(event.getTransactionId())
                .accountId(event.getAccountId())
                .clientId(event.getClientId())
                .amount(event.getAmount())
                .type(event.getType().name())
                .createdAt(event.getCreatedAt())
                .build();

        historyRepository.save(history);

        List<TransactionHistory> recent =
                historyRepository.findTop10ByClientIdOrderByCreatedAtDesc(event.getClientId());

        GroqVerdict verdict;
        try {
            String summary = buildSummary(event, recent);
            verdict = groqClient.analyze(summary);
        } catch (Exception e) {
            log.warn("Groq call failed, falling back to Z-score for tx {}", event.getTransactionId(), e);
            verdict = fallbackZScore(event, recent);
        }

        FraudAnalysisEvent result = FraudAnalysisEvent.builder()
                .transactionId(event.getTransactionId())
                .accountId(event.getAccountId())
                .clientId(event.getClientId())
                .accountNumber(event.getAccountNumber())
                .amount(event.getAmount())
                .type(event.getType())
                .createdAt(event.getCreatedAt())
                .anomalyScore(verdict.getAnomalyScore())
                .suspicious(verdict.isSuspicious())
                .reason(verdict.getReason())
                .build();

        producer.publish(result);
    }

    private String buildSummary(TransactionCreatedEvent event, List<TransactionHistory> recent) {
        try {
            var payload = Map.of(
                    "currentTransaction", Map.of(
                            "amount", event.getAmount(),
                            "type", event.getType().name(),
                            "hour", event.getCreatedAt().getHour(),
                            "dayOfWeek", event.getCreatedAt().getDayOfWeek().toString()
                    ),
                    "recentHistory", recent.stream().map(t -> Map.of(
                            "amount", t.getAmount(),
                            "type", t.getType(),
                            "createdAt", t.getCreatedAt().toString()
                    )).toList()
            );
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to build transaction summary", e);
        }
    }

    private GroqVerdict fallbackZScore(TransactionCreatedEvent event, List<TransactionHistory> recent) {
        GroqVerdict verdict = new GroqVerdict();

        if (recent.size() < 3) {
            verdict.setAnomalyScore(0.0);
            verdict.setSuspicious(false);
            verdict.setReason("Not enough history for scoring");
            return verdict;
        }

        BigDecimal mean = recent.stream()
                .map(t -> t.getAmount().abs())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(recent.size()), 4, RoundingMode.HALF_UP);

        double variance = recent.stream()
                .mapToDouble(t -> {
                    double diff = t.getAmount().abs().doubleValue() - mean.doubleValue();
                    return diff * diff;
                })
                .average()
                .orElse(0.0);

        double stddev = Math.sqrt(variance);
        double amount = event.getAmount().abs().doubleValue();

        double z = stddev == 0 ? 0 : (amount - mean.doubleValue()) / stddev;
        double absZ = Math.abs(z);

        verdict.setAnomalyScore(Math.min(absZ / 5.0, 1.0));
        verdict.setSuspicious(absZ >= 3);
        verdict.setReason(absZ >= 3
                ? "Amount deviates significantly from client's usual pattern (z=" + String.format("%.2f", z) + ")"
                : "Within normal range");

        return verdict;
    }
}
