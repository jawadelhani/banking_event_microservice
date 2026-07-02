package com.jawad.bank.agency.services;

import com.jawad.bank.agency.dtos.AgencyAlertDto;
import com.jawad.bank.agency.dtos.CreateAgencyAlertRequest;
import com.jawad.bank.agency.dtos.UpdateAgencyAlertRequest;
import com.jawad.bank.agency.entities.AgencyAlert;
import com.jawad.bank.agency.mappers.AgencyAlertMapper;
import com.jawad.bank.agency.repositories.AgencyAlertRepository;
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
class AgencyAlertServiceTest {

    @Mock
    private AgencyAlertRepository agencyAlertRepository;

    @Mock
    private AgencyAlertMapper agencyAlertMapper;

    @InjectMocks
    private AgencyAlertService agencyAlertService;

    @Test
    void findAll() {
        AgencyAlert alert = new AgencyAlert();
        AgencyAlertDto dto = new AgencyAlertDto();

        when(agencyAlertRepository.findAll()).thenReturn(List.of(alert));
        when(agencyAlertMapper.toDto(alert)).thenReturn(dto);

        List<AgencyAlertDto> result = agencyAlertService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void findByClientId() {
        UUID clientId = UUID.randomUUID();
        AgencyAlert alert = new AgencyAlert();
        AgencyAlertDto dto = new AgencyAlertDto();

        when(agencyAlertRepository.findByClientId(clientId)).thenReturn(List.of(alert));
        when(agencyAlertMapper.toDto(alert)).thenReturn(dto);

        List<AgencyAlertDto> result = agencyAlertService.findByClientId(clientId);

        assertEquals(1, result.size());
    }

    @Test
    void findById() {
        UUID id = UUID.randomUUID();
        AgencyAlert alert = new AgencyAlert();
        AgencyAlertDto dto = new AgencyAlertDto();

        when(agencyAlertRepository.findById(id)).thenReturn(Optional.of(alert));
        when(agencyAlertMapper.toDto(alert)).thenReturn(dto);

        Optional<AgencyAlertDto> result = agencyAlertService.findById(id);

        assertTrue(result.isPresent());
    }

    @Test
    void create() {
        CreateAgencyAlertRequest request = new CreateAgencyAlertRequest();
        AgencyAlert alert = new AgencyAlert();
        AgencyAlertDto dto = new AgencyAlertDto();

        when(agencyAlertMapper.toEntity(request)).thenReturn(alert);
        when(agencyAlertRepository.save(alert)).thenReturn(alert);
        when(agencyAlertMapper.toDto(alert)).thenReturn(dto);

        AgencyAlertDto result = agencyAlertService.create(request);

        assertNotNull(result);
    }

    @Test
    void update() {
        UUID id = UUID.randomUUID();
        AgencyAlert alert = new AgencyAlert();
        UpdateAgencyAlertRequest request = new UpdateAgencyAlertRequest();
        AgencyAlertDto dto = new AgencyAlertDto();

        when(agencyAlertRepository.findById(id)).thenReturn(Optional.of(alert));
        when(agencyAlertRepository.save(alert)).thenReturn(alert);
        when(agencyAlertMapper.toDto(alert)).thenReturn(dto);

        Optional<AgencyAlertDto> result = agencyAlertService.update(id, request);

        assertTrue(result.isPresent());
    }

    @Test
    void delete() {
        UUID existingId = UUID.randomUUID();
        when(agencyAlertRepository.existsById(existingId)).thenReturn(true);

        assertTrue(agencyAlertService.delete(existingId));

        UUID missingId = UUID.randomUUID();
        when(agencyAlertRepository.existsById(missingId)).thenReturn(false);

        assertFalse(agencyAlertService.delete(missingId));
    }
}