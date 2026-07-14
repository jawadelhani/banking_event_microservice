package com.jawad.bank.account.controllers;

import com.jawad.bank.account.dtos.AccountDto;
import com.jawad.bank.account.dtos.BalanceAdjustmentRequest;
import com.jawad.bank.account.dtos.CreateAccountRequest;
import com.jawad.bank.account.dtos.UpdateAccountRequest;
import com.jawad.bank.account.entities.Client;
import com.jawad.bank.account.services.AccountService;
import com.jawad.bank.account.services.ClientService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final ClientService clientService; // add this


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Iterable<AccountDto> getAllAccounts() {
        return accountService.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccount(@PathVariable UUID id) {
        return accountService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/client/{clientId}")
    public Iterable<AccountDto> getAccountsByClient(@PathVariable UUID clientId) {
        return accountService.findByClientId(clientId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountDto> getAccountByNumber(@PathVariable String accountNumber) {
        return accountService.findByAccountNumber(accountNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            UriComponentsBuilder uriBuilder) {
        if (accountService.existsByAccountNumber(request.getAccountNumber())) {
            return ResponseEntity.badRequest().body(Map.of("accountNumber", "Account number already exists"));
        }
        var accountOptional = accountService.create(request);
        if (accountOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("clientId", "Client not found"));
        }
        AccountDto accountDto = accountOptional.get();
        var uri = uriBuilder.path("/accounts/{id}").buildAndExpand(accountDto.getId()).toUri();
        return ResponseEntity.created(uri).body(accountDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> updateAccount(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAccountRequest request) {
        return accountService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {
        if (!accountService.delete(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    // AccountController
    @PutMapping("/{id}/balance")
    public ResponseEntity<AccountDto> adjustBalance(
            @PathVariable UUID id,
            @RequestBody BalanceAdjustmentRequest request) {
        return accountService.adjustBalance(id, request.getDelta())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENT')")
    public Iterable<AccountDto> me(
            @AuthenticationPrincipal Jwt jwt) {

        Client client = clientService
                .findEntityByKeycloakUserId(jwt.getSubject())
                .orElseThrow();

        return accountService.findByClientId(client.getId());
    }
}