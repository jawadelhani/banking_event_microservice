package com.jawad.bank.account.dtos;

import com.jawad.bank.account.entities.AccountStatus;
import com.jawad.bank.account.entities.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
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
