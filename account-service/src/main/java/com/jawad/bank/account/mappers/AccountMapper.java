package com.jawad.bank.account.mappers;

import com.jawad.bank.account.dtos.CreateAccountRequest;
import com.jawad.bank.account.dtos.AccountDto;
import com.jawad.bank.account.dtos.UpdateAccountRequest;
import com.jawad.bank.account.entities.Account;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountDto toDto(Account account);

    Account toEntity(CreateAccountRequest request);

    void updateAccount(UpdateAccountRequest request, @MappingTarget Account account);
}
