package com.jawad.bank.account.services;

import com.jawad.bank.account.dtos.AccountDto;
import com.jawad.bank.account.dtos.CreateAccountRequest;
import com.jawad.bank.account.dtos.UpdateAccountRequest;
import com.jawad.bank.account.entities.Account;
import com.jawad.bank.account.mappers.AccountMapper;
import com.jawad.bank.account.repositories.AccountRepository;
import com.jawad.bank.account.repositories.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    @Test
    void findAll() {
        Account account = new Account();
        AccountDto dto = new AccountDto();

        when(accountRepository.findAll()).thenReturn(List.of(account));
        when(accountMapper.toDto(account)).thenReturn(dto);

        List<AccountDto> result = accountService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void findById() {
        UUID id = UUID.randomUUID();
        Account account = new Account();
        AccountDto dto = new AccountDto();

        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        when(accountMapper.toDto(account)).thenReturn(dto);

        Optional<AccountDto> result = accountService.findById(id);

        assertTrue(result.isPresent());
    }

    @Test
    void create_whenClientExists_returnsAccount() {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setClientId(UUID.randomUUID());

        Account account = new Account();
        AccountDto dto = new AccountDto();

        when(clientRepository.existsById(request.getClientId())).thenReturn(true);
        when(accountMapper.toEntity(request)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toDto(account)).thenReturn(dto);

        Optional<AccountDto> result = accountService.create(request);

        assertTrue(result.isPresent());
    }

    @Test
    void create_whenClientDoesNotExist_returnsEmpty() {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setClientId(UUID.randomUUID());

        when(clientRepository.existsById(request.getClientId())).thenReturn(false);

        Optional<AccountDto> result = accountService.create(request);

        assertTrue(result.isEmpty());
    }

    @Test
    void update() {
        UUID id = UUID.randomUUID();
        Account account = new Account();
        UpdateAccountRequest request = new UpdateAccountRequest();
        AccountDto dto = new AccountDto();

        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toDto(account)).thenReturn(dto);

        Optional<AccountDto> result = accountService.update(id, request);

        assertTrue(result.isPresent());
    }

    @Test
    void delete_whenExists_returnsTrue() {
        UUID id = UUID.randomUUID();
        when(accountRepository.existsById(id)).thenReturn(true);

        boolean result = accountService.delete(id);

        assertTrue(result);
        verify(accountRepository).deleteById(id);
    }

    @Test
    void delete_whenNotExists_returnsFalse() {
        UUID id = UUID.randomUUID();
        when(accountRepository.existsById(id)).thenReturn(false);

        boolean result = accountService.delete(id);

        assertFalse(result);
        verify(accountRepository, never()).deleteById(any());
    }
}