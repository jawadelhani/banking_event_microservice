package com.jawad.bank.simulator.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BalanceAdjustmentRequest {
    private BigDecimal delta; // positive for CREDIT, negative for DEBIT
}