package com.example.aiservice.repositories;

import com.example.aiservice.entities.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, UUID> {
    List<TransactionHistory> findTop10ByClientIdOrderByCreatedAtDesc(UUID clientId);
    List<TransactionHistory> findByClientIdAndCreatedAtAfter(UUID clientId, LocalDateTime since);
}