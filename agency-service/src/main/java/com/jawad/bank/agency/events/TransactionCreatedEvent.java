package com.jawad.bank.agency.events;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionCreatedEvent {
    private UUID transactionId;
    private UUID accountId;
    private UUID clientId;
    private String accountNumber;
    private BigDecimal amount;
    private String type; // Using String to avoid pulling the enum
    private LocalDateTime createdAt;
}
