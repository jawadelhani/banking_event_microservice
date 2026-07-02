package com.jawad.bank.account.mappers;

import com.jawad.bank.account.dtos.AccountDto;
import com.jawad.bank.account.dtos.CreateAccountRequest;
import com.jawad.bank.account.dtos.UpdateAccountRequest;
import com.jawad.bank.account.entities.Account;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-02T11:40:32+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class AccountMapperImpl implements AccountMapper {

    @Override
    public AccountDto toDto(Account account) {
        if ( account == null ) {
            return null;
        }

        AccountDto accountDto = new AccountDto();

        return accountDto;
    }

    @Override
    public Account toEntity(CreateAccountRequest request) {
        if ( request == null ) {
            return null;
        }

        Account.AccountBuilder account = Account.builder();

        account.clientId( request.getClientId() );
        account.accountNumber( request.getAccountNumber() );
        account.balance( request.getBalance() );
        account.accountType( request.getAccountType() );
        account.status( request.getStatus() );

        return account.build();
    }

    @Override
    public void updateAccount(UpdateAccountRequest request, Account account) {
        if ( request == null ) {
            return;
        }

        account.setBalance( request.getBalance() );
        account.setAccountType( request.getAccountType() );
        account.setStatus( request.getStatus() );
    }
}
