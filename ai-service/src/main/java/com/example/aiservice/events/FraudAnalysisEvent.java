package com.example.aiservice.events;

import com.example.aiservice.entities.TransactionType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudAnalysisEvent {
    private UUID transactionId;
    private UUID accountId;
    private UUID clientId;
    private String accountNumber;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime createdAt;
    private double anomalyScore;
    private boolean suspicious;
    private String reason;
}
