package com.jawad.bank.simulator.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AccountDto {
    private UUID id;
    private UUID clientId;
    private String accountNumber;
    private BigDecimal balance;
    private String accountType;
    private String status;
}