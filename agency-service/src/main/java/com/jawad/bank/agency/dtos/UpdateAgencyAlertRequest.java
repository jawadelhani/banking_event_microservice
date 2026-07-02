package com.jawad.bank.agency.dtos;

import com.jawad.bank.agency.entities.Criticality;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAgencyAlertRequest {

    @NotNull
    private Criticality criticality;

    private boolean seenByAgent;
}
