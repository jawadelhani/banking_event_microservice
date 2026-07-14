package com.example.aiservice.events;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardSuggestionEvent {
    private UUID transactionId;
    private UUID accountId;
    private UUID clientId;
    private String accountNumber;
    private BigDecimal weeklyAverage;
    private String suggestedCard;
    private String message;
    private LocalDateTime createdAt;
}
