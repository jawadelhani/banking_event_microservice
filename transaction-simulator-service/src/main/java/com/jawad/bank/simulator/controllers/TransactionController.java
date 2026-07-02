package com.jawad.bank.simulator.controllers;

import com.jawad.bank.simulator.dtos.CreateTransactionRequest;
import com.jawad.bank.simulator.dtos.SimulateTransactionsRequest;
import com.jawad.bank.simulator.dtos.TransactionDto;
import com.jawad.bank.simulator.services.TransactionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public Iterable<TransactionDto> getAllTransactions() {
        return transactionService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransaction(@PathVariable UUID id) {
        return transactionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/account/{accountId}")
    public Iterable<TransactionDto> getByAccount(@PathVariable UUID accountId) {
        return transactionService.findByAccountId(accountId);
    }

    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request,
            UriComponentsBuilder uriBuilder) {
        var dto = transactionService.create(request);
        var uri = uriBuilder.path("/transactions/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    /**
     * Triggers automatic simulation of fake banking movements.
     * Example body: { "accountIds": ["uuid-1", "uuid-2"], "count": 5 }
     */
    @PostMapping("/simulate")
    public Iterable<TransactionDto> simulateTransactions(
            @Valid @RequestBody SimulateTransactionsRequest request) {
        return transactionService.simulate(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID id) {
        if (!transactionService.delete(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
