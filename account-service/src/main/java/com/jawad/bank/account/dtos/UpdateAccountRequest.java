package com.jawad.bank.account.dtos;

import com.jawad.bank.account.entities.AccountStatus;
import com.jawad.bank.account.entities.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateAccountRequest {

    @NotNull
    private BigDecimal balance;

    @NotNull
    private AccountType accountType;

    @NotNull
    private AccountStatus status;
}
