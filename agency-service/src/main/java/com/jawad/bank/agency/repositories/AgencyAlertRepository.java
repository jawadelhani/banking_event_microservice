package com.jawad.bank.agency.repositories;

import com.jawad.bank.agency.entities.AgencyAlert;
import com.jawad.bank.agency.entities.Criticality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AgencyAlertRepository extends JpaRepository<AgencyAlert, UUID> {

    List<AgencyAlert> findByClientId(UUID clientId);

    List<AgencyAlert> findByClientIdAndSeenByAgentFalse(UUID clientId);

    List<AgencyAlert> findByCriticality(Criticality criticality);
}
