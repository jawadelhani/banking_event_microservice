package com.jawad.bank.account.services;

import com.jawad.bank.account.dtos.AccountDto;
import com.jawad.bank.account.dtos.CreateAccountRequest;
import com.jawad.bank.account.dtos.UpdateAccountRequest;
import com.jawad.bank.account.entities.Account;
import com.jawad.bank.account.mappers.AccountMapper;
import com.jawad.bank.account.repositories.AccountRepository;
import com.jawad.bank.account.repositories.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final AccountMapper accountMapper;

    public List<AccountDto> findAll() {
        return accountRepository.findAll()
                .stream()
                .map(accountMapper::toDto)
                .toList();
    }

    public List<AccountDto> findByClientId(UUID clientId) {
        return accountRepository.findByClientId(clientId)
                .stream()
                .map(accountMapper::toDto)
                .toList();
    }

    public Optional<AccountDto> findById(UUID id) {
        return accountRepository.findById(id).map(accountMapper::toDto);
    }

    public Optional<AccountDto> findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber).map(accountMapper::toDto);
    }

    @Transactional
    public Optional<AccountDto> create(CreateAccountRequest request) {
        if (!clientRepository.existsById(request.getClientId())) {
            return Optional.empty();
        }
        Account account = accountMapper.toEntity(request);
        return Optional.of(accountMapper.toDto(accountRepository.save(account)));
    }

    @Transactional
    public Optional<AccountDto> update(UUID id, UpdateAccountRequest request) {
        return accountRepository.findById(id)
                .map(account -> {
                    accountMapper.updateAccount(request, account);
                    return accountMapper.toDto(accountRepository.save(account));
                });
    }

    @Transactional
    public boolean delete(UUID id) {
        if (!accountRepository.existsById(id)) {
            return false;
        }
        accountRepository.deleteById(id);
        return true;
    }

    public boolean existsByAccountNumber(String accountNumber) {
        return accountRepository.existsByAccountNumber(accountNumber);
    }
}
