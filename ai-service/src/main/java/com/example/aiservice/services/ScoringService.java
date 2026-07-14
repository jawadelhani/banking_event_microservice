package com.example.aiservice.services;

import com.example.aiservice.dtos.CardSuggestionVerdict;
import com.example.aiservice.entities.TransactionHistory;
import com.example.aiservice.events.CardSuggestionEvent;
import com.example.aiservice.events.TransactionCreatedEvent;
import com.example.aiservice.groq.GroqClient;
import com.example.aiservice.messaging.CardSuggestionProducer;
import com.example.aiservice.repositories.TransactionHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoringService {

    private static final int WEEKLY_WINDOW_DAYS = 7;

    private final TransactionHistoryRepository historyRepository;
    private final GroqClient groqClient;
    private final CardSuggestionProducer producer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void handle(TransactionCreatedEvent event) {
        if (event.getClientId() == null) {
            log.warn("Skipping card suggestion — missing clientId for tx {}", event.getTransactionId());
            return;
        }

        TransactionHistory history = TransactionHistory.builder()
                .transactionId(event.getTransactionId())
                .accountId(event.getAccountId())
                .clientId(event.getClientId())
                .amount(event.getAmount())
                .type(event.getType().name())
                .createdAt(event.getCreatedAt())
                .build();

        historyRepository.save(history);

        BigDecimal weeklyAverage = computeWeeklyAverage(event.getClientId());
        int transactionCount = countWeeklyTransactions(event.getClientId());

        CardSuggestionVerdict verdict;
        try {
            String summary = buildSummary(event.getClientId(), weeklyAverage, transactionCount);
            verdict = groqClient.suggestCardTier(summary);
        } catch (Exception e) {
            log.warn("Groq call failed, falling back to tier rules for tx {}", event.getTransactionId(), e);
            verdict = fallbackTier(weeklyAverage);
        }

        CardSuggestionEvent result = CardSuggestionEvent.builder()
                .transactionId(event.getTransactionId())
                .accountId(event.getAccountId())
                .clientId(event.getClientId())
                .accountNumber(event.getAccountNumber())
                .weeklyAverage(weeklyAverage)
                .suggestedCard(verdict.getSuggestedCard())
                .message(verdict.getMessage())
                .createdAt(LocalDateTime.now())
                .build();

        producer.publish(result);
    }

    private BigDecimal computeWeeklyAverage(UUID clientId) {
        List<TransactionHistory> weekTransactions = loadWeeklyTransactions(clientId);

        return weekTransactions.stream()
                .filter(t -> "DEBIT".equals(t.getType()))
                .map(t -> t.getAmount().abs())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int countWeeklyTransactions(UUID clientId) {
        return loadWeeklyTransactions(clientId).size();
    }

    private List<TransactionHistory> loadWeeklyTransactions(UUID clientId) {
        LocalDateTime since = LocalDateTime.now().minusDays(WEEKLY_WINDOW_DAYS);
        return historyRepository.findByClientIdAndCreatedAtAfter(clientId, since);
    }

    private String buildSummary(UUID clientId, BigDecimal weeklyAverage, int transactionCount) {
        try {
            var payload = Map.of(
                    "clientId", clientId.toString(),
                    "weeklyAverage", weeklyAverage,
                    "transactionCount", transactionCount
            );
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to build spending summary", e);
        }
    }

    private CardSuggestionVerdict fallbackTier(BigDecimal weeklyAverage) {
        CardSuggestionVerdict verdict = new CardSuggestionVerdict();
        double avg = weeklyAverage.doubleValue();

        if (avg < 1000) {
            verdict.setSuggestedCard("STANDARD");
            verdict.setMessage("Based on your recent spending, our Standard card is a great fit for you.");
        } else if (avg <= 10000) {
            verdict.setSuggestedCard("SILVER");
            verdict.setMessage("Your weekly spending qualifies you for our Silver card with extra perks.");
        } else {
            verdict.setSuggestedCard("GOLD");
            verdict.setMessage("Your spending level makes you eligible for our premium Gold card.");
        }

        return verdict;
    }
}
