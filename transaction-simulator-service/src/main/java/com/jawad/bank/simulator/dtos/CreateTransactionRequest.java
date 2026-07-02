package com.jawad.bank.simulator.dtos;

import com.jawad.bank.simulator.entities.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CreateTransactionRequest {

    @NotNull
    private UUID accountId;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private TransactionType type;
}
