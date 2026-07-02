package com.jawad.bank.account.mappers;

import com.jawad.bank.account.dtos.CreateClientRequest;
import com.jawad.bank.account.dtos.ClientDto;
import com.jawad.bank.account.dtos.UpdateClientRequest;
import com.jawad.bank.account.entities.Client;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientDto toDto(Client client);

    Client toEntity(CreateClientRequest request);

    void updateClient(UpdateClientRequest request, @MappingTarget Client client);
}
