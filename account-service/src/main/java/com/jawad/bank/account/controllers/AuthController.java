package com.jawad.bank.account.controllers;

import com.jawad.bank.account.dtos.ClientDto;
import com.jawad.bank.account.dtos.LoginRequest;
import com.jawad.bank.account.dtos.LoginResponse;
import com.jawad.bank.account.dtos.RegisterRequest;
import com.jawad.bank.account.services.AuthenticationService;
import com.jawad.bank.account.services.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegistrationService registrationService;
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ClientDto> register(
            @Valid @RequestBody RegisterRequest request) {

        ClientDto dto = registrationService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request) {

        return authenticationService.login(request);
    }

}

