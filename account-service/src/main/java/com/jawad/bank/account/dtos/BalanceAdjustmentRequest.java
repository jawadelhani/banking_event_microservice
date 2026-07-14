package com.jawad.bank.account.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceAdjustmentRequest {
    private BigDecimal delta; // positive for CREDIT, negative for DEBIT
}