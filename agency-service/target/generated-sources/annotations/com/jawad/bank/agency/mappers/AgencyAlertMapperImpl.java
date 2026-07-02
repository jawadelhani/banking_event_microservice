package com.jawad.bank.agency.mappers;

import com.jawad.bank.agency.dtos.AgencyAlertDto;
import com.jawad.bank.agency.dtos.CreateAgencyAlertRequest;
import com.jawad.bank.agency.dtos.UpdateAgencyAlertRequest;
import com.jawad.bank.agency.entities.AgencyAlert;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-02T12:05:07+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class AgencyAlertMapperImpl implements AgencyAlertMapper {

    @Override
    public AgencyAlertDto toDto(AgencyAlert alert) {
        if ( alert == null ) {
            return null;
        }

        AgencyAlertDto agencyAlertDto = new AgencyAlertDto();

        return agencyAlertDto;
    }

    @Override
    public AgencyAlert toEntity(CreateAgencyAlertRequest request) {
        if ( request == null ) {
            return null;
        }

        AgencyAlert.AgencyAlertBuilder agencyAlert = AgencyAlert.builder();

        agencyAlert.clientId( request.getClientId() );
        agencyAlert.txId( request.getTxId() );
        agencyAlert.criticality( request.getCriticality() );

        return agencyAlert.build();
    }

    @Override
    public void updateAlert(UpdateAgencyAlertRequest request, AgencyAlert alert) {
        if ( request == null ) {
            return;
        }

        alert.setCriticality( request.getCriticality() );
        alert.setSeenByAgent( request.isSeenByAgent() );
    }
}
