package com.jawad.bank.account.mappers;

import com.jawad.bank.account.dtos.ClientDto;
import com.jawad.bank.account.dtos.CreateClientRequest;
import com.jawad.bank.account.dtos.UpdateClientRequest;
import com.jawad.bank.account.entities.Client;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-02T12:18:23+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class ClientMapperImpl implements ClientMapper {

    @Override
    public ClientDto toDto(Client client) {
        if ( client == null ) {
            return null;
        }

        UUID id = null;
        String cin = null;
        String fullName = null;
        String email = null;
        String phone = null;
        BigDecimal monthlyIncome = null;
        LocalDateTime createdAt = null;

        id = client.getId();
        cin = client.getCin();
        fullName = client.getFullName();
        email = client.getEmail();
        phone = client.getPhone();
        monthlyIncome = client.getMonthlyIncome();
        createdAt = client.getCreatedAt();

        ClientDto clientDto = new ClientDto( id, cin, fullName, email, phone, monthlyIncome, createdAt );

        return clientDto;
    }

    @Override
    public Client toEntity(CreateClientRequest request) {
        if ( request == null ) {
            return null;
        }

        Client.ClientBuilder client = Client.builder();

        client.cin( request.getCin() );
        client.fullName( request.getFullName() );
        client.email( request.getEmail() );
        client.phone( request.getPhone() );
        client.monthlyIncome( request.getMonthlyIncome() );

        return client.build();
    }

    @Override
    public void updateClient(UpdateClientRequest request, Client client) {
        if ( request == null ) {
            return;
        }

        client.setFullName( request.getFullName() );
        client.setEmail( request.getEmail() );
        client.setPhone( request.getPhone() );
        client.setMonthlyIncome( request.getMonthlyIncome() );
    }
}
