package com.jawad.bank.agency.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "agency_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgencyTransaction {
    @Id
    private UUID transactionId;
    private UUID accountId;
    private UUID clientId;
    private String accountNumber;
    private BigDecimal amount;
    private String type;
    private LocalDateTime createdAt;
}
