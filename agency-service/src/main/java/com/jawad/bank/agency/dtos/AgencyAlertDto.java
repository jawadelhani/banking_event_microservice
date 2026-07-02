package com.jawad.bank.agency.dtos;

import com.jawad.bank.agency.entities.Criticality;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AgencyAlertDto {
    private UUID id;
    private UUID clientId;
    private UUID txId;
    private Criticality criticality;
    private boolean seenByAgent;
    private LocalDateTime createdAt;
}
