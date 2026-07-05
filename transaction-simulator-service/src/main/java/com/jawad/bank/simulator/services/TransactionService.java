package com.jawad.bank.simulator.services;

import com.jawad.bank.simulator.config.SimulatorProperties;
import com.jawad.bank.simulator.dtos.CreateTransactionRequest;
import com.jawad.bank.simulator.dtos.SimulateTransactionsRequest;
import com.jawad.bank.simulator.dtos.TransactionDto;
import com.jawad.bank.simulator.entities.Transaction;
import com.jawad.bank.simulator.entities.TransactionType;
import com.jawad.bank.simulator.mappers.TransactionMapper;
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
    public TransactionDto create(CreateTransactionRequest request) {
        Transaction transaction = transactionMapper.toEntity(request);
        transaction.setAmount(signedAmount(request.getAmount(), request.getType()));
        return transactionMapper.toDto(transactionRepository.save(transaction));
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

            generated.add(transactionRepository.save(transaction));
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
