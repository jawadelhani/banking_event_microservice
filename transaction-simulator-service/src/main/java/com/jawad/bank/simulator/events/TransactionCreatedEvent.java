package com.jawad.bank.simulator.events;


import com.jawad.bank.simulator.entities.TransactionType;
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

    private TransactionType type;

    private LocalDateTime createdAt;

}
