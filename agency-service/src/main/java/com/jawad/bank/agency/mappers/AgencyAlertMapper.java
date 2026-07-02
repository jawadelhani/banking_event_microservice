package com.jawad.bank.agency.mappers;

import com.jawad.bank.agency.dtos.AgencyAlertDto;
import com.jawad.bank.agency.dtos.CreateAgencyAlertRequest;
import com.jawad.bank.agency.dtos.UpdateAgencyAlertRequest;
import com.jawad.bank.agency.entities.AgencyAlert;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AgencyAlertMapper {

    AgencyAlertDto toDto(AgencyAlert alert);

    AgencyAlert toEntity(CreateAgencyAlertRequest request);

    void updateAlert(UpdateAgencyAlertRequest request, @MappingTarget AgencyAlert alert);
}
