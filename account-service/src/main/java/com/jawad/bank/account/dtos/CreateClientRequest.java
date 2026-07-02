package com.jawad.bank.account.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateClientRequest {

    @NotBlank
    private String cin;

    @NotBlank
    private String fullName;

    @NotBlank
    @Email
    private String email;

    private String phone;

    @NotNull
    private BigDecimal monthlyIncome;
}
