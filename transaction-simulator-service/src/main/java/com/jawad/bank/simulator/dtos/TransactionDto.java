package com.jawad.bank.simulator.dtos;

import com.jawad.bank.simulator.entities.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
    private UUID id;
    private UUID accountId;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime createdAt;
}
