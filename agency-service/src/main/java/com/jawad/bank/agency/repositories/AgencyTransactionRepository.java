package com.jawad.bank.agency.repositories;

import com.jawad.bank.agency.entities.AgencyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AgencyTransactionRepository extends JpaRepository<AgencyTransaction, UUID> {
    List<AgencyTransaction> findByClientId(UUID clientId);
}
