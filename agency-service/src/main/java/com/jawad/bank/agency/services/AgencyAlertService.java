package com.jawad.bank.agency.services;

import com.jawad.bank.agency.dtos.AgencyAlertDto;
import com.jawad.bank.agency.dtos.CreateAgencyAlertRequest;
import com.jawad.bank.agency.dtos.UpdateAgencyAlertRequest;
import com.jawad.bank.agency.entities.Criticality;
import com.jawad.bank.agency.mappers.AgencyAlertMapper;
import com.jawad.bank.agency.repositories.AgencyAlertRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AgencyAlertService {

    private final AgencyAlertRepository agencyAlertRepository;
    private final AgencyAlertMapper agencyAlertMapper;

    public List<AgencyAlertDto> findAll() {
        return agencyAlertRepository.findAll()
                .stream()
                .map(agencyAlertMapper::toDto)
                .toList();
    }

    public List<AgencyAlertDto> findByClientId(UUID clientId) {
        return agencyAlertRepository.findByClientId(clientId)
                .stream()
                .map(agencyAlertMapper::toDto)
                .toList();
    }

    public List<AgencyAlertDto> findUnseenByClientId(UUID clientId) {
        return agencyAlertRepository.findByClientIdAndSeenByAgentFalse(clientId)
                .stream()
                .map(agencyAlertMapper::toDto)
                .toList();
    }

    public List<AgencyAlertDto> findByCriticality(Criticality criticality) {
        return agencyAlertRepository.findByCriticality(criticality)
                .stream()
                .map(agencyAlertMapper::toDto)
                .toList();
    }

    public Optional<AgencyAlertDto> findById(UUID id) {
        return agencyAlertRepository.findById(id).map(agencyAlertMapper::toDto);
    }

    @Transactional
    public AgencyAlertDto create(CreateAgencyAlertRequest request) {
        var alert = agencyAlertMapper.toEntity(request);
        return agencyAlertMapper.toDto(agencyAlertRepository.save(alert));
    }

    @Transactional
    public Optional<AgencyAlertDto> update(UUID id, UpdateAgencyAlertRequest request) {
        return agencyAlertRepository.findById(id)
                .map(alert -> {
                    agencyAlertMapper.updateAlert(request, alert);
                    return agencyAlertMapper.toDto(agencyAlertRepository.save(alert));
                });
    }

    @Transactional
    public Optional<AgencyAlertDto> markAsSeen(UUID id) {
        return agencyAlertRepository.findById(id)
                .map(alert -> {
                    alert.setSeenByAgent(true);
                    return agencyAlertMapper.toDto(agencyAlertRepository.save(alert));
                });
    }

    @Transactional
    public boolean delete(UUID id) {
        if (!agencyAlertRepository.existsById(id)) {
            return false;
        }
        agencyAlertRepository.deleteById(id);
        return true;
    }
}
