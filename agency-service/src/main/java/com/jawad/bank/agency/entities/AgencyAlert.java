package com.jawad.bank.agency.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "agency_alerts")
public class AgencyAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "tx_id", nullable = false)
    private UUID txId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Criticality criticality;

    @Column(name = "seen_by_agent", nullable = false)
    @Builder.Default
    private boolean seenByAgent = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
