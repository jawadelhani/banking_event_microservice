package com.jawad.bank.account.services;

import com.jawad.bank.account.dtos.RegisterRequest;

public interface KeycloakService {

    String createClient(RegisterRequest request);

}