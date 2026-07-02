package com.jawad.bank.account.controllers;

import com.jawad.bank.account.dtos.ClientDto;
import com.jawad.bank.account.dtos.CreateClientRequest;
import com.jawad.bank.account.dtos.UpdateClientRequest;
import com.jawad.bank.account.services.ClientService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public Iterable<ClientDto> getAllClients(
            @RequestParam(required = false, defaultValue = "fullName", name = "sort") String sort) {
        return clientService.findAll(sort);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClient(@PathVariable UUID id) {
        return clientService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cin/{cin}")
    public ResponseEntity<ClientDto> getClientByCin(@PathVariable String cin) {
        return clientService.findByCin(cin)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createClient(
            @Valid @RequestBody CreateClientRequest request,
            UriComponentsBuilder uriBuilder) {
        if (clientService.existsByCin(request.getCin())) {
            return ResponseEntity.badRequest().body(Map.of("cin", "CIN already registered"));
        }
        var clientDto = clientService.create(request);
        var uri = uriBuilder.path("/clients/{id}").buildAndExpand(clientDto.getId()).toUri();
        return ResponseEntity.created(uri).body(clientDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDto> updateClient(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateClientRequest request) {
        return clientService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id) {
        if (!clientService.delete(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
