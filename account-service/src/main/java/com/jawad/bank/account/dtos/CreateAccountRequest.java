package com.jawad.bank.account.dtos;

import com.jawad.bank.account.entities.AccountStatus;
import com.jawad.bank.account.entities.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CreateAccountRequest {

    @NotNull
    private UUID clientId;

    @NotBlank
    private String accountNumber;

    @NotNull
    private BigDecimal balance;

    @NotNull
    private AccountType accountType;

    @NotNull
    private AccountStatus status;
}
