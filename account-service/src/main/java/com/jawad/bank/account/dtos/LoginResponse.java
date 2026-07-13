package com.jawad.bank.account.dtos;

import lombok.Data;

@Data
public class LoginResponse {

    private String accessToken;

    private String refreshToken;

    private Long expiresIn;

    private String tokenType;
}