package com.jawad.bank.account.services;

import com.jawad.bank.account.dtos.ClientDto;
import com.jawad.bank.account.dtos.CreateClientRequest;
import com.jawad.bank.account.dtos.UpdateClientRequest;
import com.jawad.bank.account.entities.Client;
import com.jawad.bank.account.mappers.ClientMapper;
import com.jawad.bank.account.repositories.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ClientService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("fullName", "cin", "createdAt");

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public List<ClientDto> findAll(String sort) {
        if (!ALLOWED_SORT_FIELDS.contains(sort)) {
            sort = "fullName";
        }
        return clientRepository.findAll(Sort.by(sort))
                .stream()
                .map(clientMapper::toDto)
                .toList();
    }

    public Optional<ClientDto> findById(UUID id) {
        return clientRepository.findById(id).map(clientMapper::toDto);
    }

    public Optional<ClientDto> findByCin(String cin) {
        return clientRepository.findByCin(cin).map(clientMapper::toDto);
    }

    @Transactional
    public ClientDto create(CreateClientRequest request) {
        Client client = clientMapper.toEntity(request);
        return clientMapper.toDto(clientRepository.save(client));
    }

    @Transactional
    public Optional<ClientDto> update(UUID id, UpdateClientRequest request) {
        return clientRepository.findById(id)
                .map(client -> {
                    clientMapper.updateClient(request, client);
                    return clientMapper.toDto(clientRepository.save(client));
                });
    }

    @Transactional
    public boolean delete(UUID id) {
        if (!clientRepository.existsById(id)) {
            return false;
        }
        clientRepository.deleteById(id);
        return true;
    }

    public boolean existsByCin(String cin) {
        return clientRepository.existsByCin(cin);
    }
}
