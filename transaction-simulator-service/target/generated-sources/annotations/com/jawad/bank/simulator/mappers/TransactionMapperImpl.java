package com.jawad.bank.simulator.mappers;

import com.jawad.bank.simulator.dtos.CreateTransactionRequest;
import com.jawad.bank.simulator.dtos.TransactionDto;
import com.jawad.bank.simulator.entities.Transaction;
import com.jawad.bank.simulator.entities.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-02T12:15:13+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class TransactionMapperImpl implements TransactionMapper {

    @Override
    public TransactionDto toDto(Transaction transaction) {
        if ( transaction == null ) {
            return null;
        }

        UUID id = null;
        UUID accountId = null;
        BigDecimal amount = null;
        TransactionType type = null;
        LocalDateTime createdAt = null;

        id = transaction.getId();
        accountId = transaction.getAccountId();
        amount = transaction.getAmount();
        type = transaction.getType();
        createdAt = transaction.getCreatedAt();

        TransactionDto transactionDto = new TransactionDto( id, accountId, amount, type, createdAt );

        return transactionDto;
    }

    @Override
    public Transaction toEntity(CreateTransactionRequest request) {
        if ( request == null ) {
            return null;
        }

        Transaction.TransactionBuilder transaction = Transaction.builder();

        transaction.accountId( request.getAccountId() );
        transaction.amount( request.getAmount() );
        transaction.type( request.getType() );

        return transaction.build();
    }
}
