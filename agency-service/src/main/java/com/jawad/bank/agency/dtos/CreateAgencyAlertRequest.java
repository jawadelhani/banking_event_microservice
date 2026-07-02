package com.jawad.bank.agency.dtos;

import com.jawad.bank.agency.entities.Criticality;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateAgencyAlertRequest {

    @NotNull
    private UUID clientId;

    @NotNull
    private UUID txId;

    @NotNull
    private Criticality criticality;
}
