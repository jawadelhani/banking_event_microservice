package com.jawad.bank.account.repositories;

import com.jawad.bank.account.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    boolean existsByCin(String cin);

    Optional<Client> findByCin(String cin);
}
