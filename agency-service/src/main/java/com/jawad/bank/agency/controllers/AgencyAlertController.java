package com.jawad.bank.agency.controllers;

import com.jawad.bank.agency.dtos.AgencyAlertDto;
import com.jawad.bank.agency.dtos.CreateAgencyAlertRequest;
import com.jawad.bank.agency.dtos.UpdateAgencyAlertRequest;
import com.jawad.bank.agency.entities.Criticality;
import com.jawad.bank.agency.services.AgencyAlertService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/alerts")
public class AgencyAlertController {

    private final AgencyAlertService agencyAlertService;

    @GetMapping
    public Iterable<AgencyAlertDto> getAllAlerts() {
        return agencyAlertService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgencyAlertDto> getAlert(@PathVariable UUID id) {
        return agencyAlertService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{clientId}")
    public Iterable<AgencyAlertDto> getByClient(@PathVariable UUID clientId) {
        return agencyAlertService.findByClientId(clientId);
    }

    @GetMapping("/client/{clientId}/unseen")
    public Iterable<AgencyAlertDto> getUnseenByClient(@PathVariable UUID clientId) {
        return agencyAlertService.findUnseenByClientId(clientId);
    }

    @GetMapping("/criticality/{criticality}")
    public Iterable<AgencyAlertDto> getByCriticality(@PathVariable Criticality criticality) {
        return agencyAlertService.findByCriticality(criticality);
    }

    @PostMapping
    public ResponseEntity<AgencyAlertDto> createAlert(
            @Valid @RequestBody CreateAgencyAlertRequest request,
            UriComponentsBuilder uriBuilder) {
        AgencyAlertDto dto = agencyAlertService.create(request);
        var uri = uriBuilder.path("/alerts/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgencyAlertDto> updateAlert(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAgencyAlertRequest request) {
        return agencyAlertService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/seen")
    public ResponseEntity<AgencyAlertDto> markAsSeen(@PathVariable UUID id) {
        return agencyAlertService.markAsSeen(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable UUID id) {
        if (!agencyAlertService.delete(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
