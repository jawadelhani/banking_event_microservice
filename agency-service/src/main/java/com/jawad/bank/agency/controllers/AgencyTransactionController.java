package com.jawad.bank.agency.controllers;

import com.jawad.bank.agency.clients.AccountClient;
import com.jawad.bank.agency.dtos.ClientDto;
import com.jawad.bank.agency.entities.AgencyTransaction;
import com.jawad.bank.agency.repositories.AgencyTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class AgencyTransactionController {

    private final AgencyTransactionRepository agencyTransactionRepository;
    private final AccountClient accountClient;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AgencyTransaction> getAllTransactions() {
        return agencyTransactionRepository.findAll();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENT')")
    public List<AgencyTransaction> getMyTransactions(@AuthenticationPrincipal Jwt jwt) {
        ClientDto client = accountClient.getCurrentClient("Bearer " + jwt.getTokenValue());
        UUID clientId = client.getId();
        return agencyTransactionRepository.findByClientId(clientId);
    }
}
