package com.jawad.bank.account.dtos;

import com.jawad.bank.account.entities.AccountStatus;
import com.jawad.bank.account.entities.AccountType;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
    private UUID id;
    private UUID clientId;
    private String accountNumber;
    private BigDecimal balance;
    private AccountType accountType;
    private AccountStatus status;
}