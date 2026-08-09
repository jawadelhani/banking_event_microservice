package com.jawad.bank.agency.dtos;

import com.jawad.bank.agency.entities.Criticality;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
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
