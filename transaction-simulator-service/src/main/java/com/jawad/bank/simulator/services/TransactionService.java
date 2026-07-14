package com.jawad.bank.simulator.services;

import com.jawad.bank.simulator.clients.AccountClient;
import com.jawad.bank.simulator.config.SimulatorProperties;
import com.jawad.bank.simulator.dtos.*;
import com.jawad.bank.simulator.entities.Transaction;
import com.jawad.bank.simulator.entities.TransactionType;
import com.jawad.bank.simulator.events.TransactionCreatedEvent;
import com.jawad.bank.simulator.mappers.TransactionMapper;
import com.jawad.bank.simulator.messaging.TransactionProducer;
import com.jawad.bank.simulator.repositories.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final SimulatorProperties simulatorProperties;
    private final TransactionProducer transactionProducer;
    private final AccountClient accountClient;

    private final Random random = new Random();

    public List<TransactionDto> findAll() {
        return transactionRepository.findAll()
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    public List<TransactionDto> findByAccountId(UUID accountId) {
        return transactionRepository.findByAccountId(accountId)
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    public Optional<TransactionDto> findById(UUID id) {
        return transactionRepository.findById(id).map(transactionMapper::toDto);
    }

    @Transactional
    public TransactionDto create(CreateTransactionRequest request, String authHeader) {
        Transaction transaction = transactionMapper.toEntity(request);
        BigDecimal signedAmount = signedAmount(request.getAmount(), request.getType());
        transaction.setAmount(signedAmount);

        Transaction saved = transactionRepository.save(transaction);

        // Apply the balance change on account-service
        AccountDto account = accountClient.adjustBalance(
                saved.getAccountId(),
                new BalanceAdjustmentRequest(signedAmount),
                authHeader
        );

        // Build and publish the full event with clientId/accountNumber populated
        TransactionCreatedEvent event = TransactionCreatedEvent.builder()
                .transactionId(saved.getId())
                .accountId(saved.getAccountId())
                .clientId(account.getClientId())
                .accountNumber(account.getAccountNumber())
                .amount(saved.getAmount())
                .type(saved.getType())
                .createdAt(saved.getCreatedAt())
                .build();

        transactionProducer.publish(event);

        return transactionMapper.toDto(saved);
    }

    /**
     * Simulates fake core-banking movements:
     * 1. Picks a random account from the provided list
     * 2. Generates a random amount between min/max (configured in application.yml)
     * 3. Randomly chooses DEBIT or CREDIT
     * 4. Persists the movement locally (Kafka publish comes in a later sprint)
     */
    @Transactional
    public List<TransactionDto> simulate(SimulateTransactionsRequest request) {
        List<UUID> accountIds = request.getAccountIds();
        ArrayList<Transaction> generated = new ArrayList<Transaction>();

        for (int i = 0; i < request.getCount(); i++) {
            UUID accountId = accountIds.get(random.nextInt(accountIds.size()));
            TransactionType type = random.nextBoolean() ? TransactionType.DEBIT : TransactionType.CREDIT;
            BigDecimal amount = randomAmount();

            Transaction transaction = Transaction.builder()
                    .accountId(accountId)
                    .type(type)
                    .amount(signedAmount(amount, type))
                    .build();

            Transaction saved = transactionRepository.save(transaction);

            generated.add(saved);

            TransactionCreatedEvent event = TransactionCreatedEvent.builder()
                    .transactionId(saved.getId())
                    .accountId(saved.getAccountId())
                    .amount(saved.getAmount())
                    .type(saved.getType())
                    .createdAt(saved.getCreatedAt())
                    .build();

            transactionProducer.publish(event);
        }

        return generated.stream().map(transactionMapper::toDto).toList();
    }

    @Transactional
    public boolean delete(UUID id) {
        if (!transactionRepository.existsById(id)) {
            return false;
        }
        transactionRepository.deleteById(id);
        return true;
    }

    private BigDecimal randomAmount() {
        BigDecimal min = simulatorProperties.getMinAmount();
        BigDecimal max = simulatorProperties.getMaxAmount();
        BigDecimal range = max.subtract(min);
        BigDecimal randomValue = BigDecimal.valueOf(random.nextDouble()).multiply(range).add(min);
        return randomValue.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal signedAmount(BigDecimal amount, TransactionType type) {
        return type == TransactionType.DEBIT ? amount.negate() : amount;
    }
}
