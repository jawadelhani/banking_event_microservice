package com.jawad.bank.simulator.services;

import com.jawad.bank.simulator.config.SimulatorProperties;
import com.jawad.bank.simulator.dtos.CreateTransactionRequest;
import com.jawad.bank.simulator.dtos.SimulateTransactionsRequest;
import com.jawad.bank.simulator.dtos.TransactionDto;
import com.jawad.bank.simulator.entities.Transaction;
import com.jawad.bank.simulator.entities.TransactionType;
import com.jawad.bank.simulator.mappers.TransactionMapper;
import com.jawad.bank.simulator.repositories.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private SimulatorProperties simulatorProperties;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void findAll() {
        Transaction transaction = new Transaction();
        TransactionDto dto = new TransactionDto();

        when(transactionRepository.findAll()).thenReturn(List.of(transaction));
        when(transactionMapper.toDto(transaction)).thenReturn(dto);

        List<TransactionDto> result = transactionService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void findById() {
        UUID id = UUID.randomUUID();
        Transaction transaction = new Transaction();
        TransactionDto dto = new TransactionDto();

        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
        when(transactionMapper.toDto(transaction)).thenReturn(dto);

        Optional<TransactionDto> result = transactionService.findById(id);

        assertTrue(result.isPresent());
    }

    @Test
    void create() {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAmount(BigDecimal.valueOf(100));
        request.setType(TransactionType.CREDIT);

        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.CREDIT);
        TransactionDto dto = new TransactionDto();

        when(transactionMapper.toEntity(request)).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        when(transactionMapper.toDto(transaction)).thenReturn(dto);

        TransactionDto result = transactionService.create(request);

        assertNotNull(result);
    }

    @Test
    void simulate() {
        SimulateTransactionsRequest request = new SimulateTransactionsRequest();
        request.setAccountIds(List.of(UUID.randomUUID()));
        request.setCount(2);

        when(simulatorProperties.getMinAmount()).thenReturn(BigDecimal.valueOf(10));
        when(simulatorProperties.getMaxAmount()).thenReturn(BigDecimal.valueOf(500));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionMapper.toDto(any(Transaction.class))).thenReturn(new TransactionDto());

        List<TransactionDto> result = transactionService.simulate(request);

        assertEquals(2, result.size());
    }

    @Test
    void delete() {
        UUID existingId = UUID.randomUUID();
        when(transactionRepository.existsById(existingId)).thenReturn(true);

        assertTrue(transactionService.delete(existingId));

        UUID missingId = UUID.randomUUID();
        when(transactionRepository.existsById(missingId)).thenReturn(false);

        assertFalse(transactionService.delete(missingId));
    }
}