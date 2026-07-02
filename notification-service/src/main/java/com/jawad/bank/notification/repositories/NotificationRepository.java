package com.jawad.bank.notification.repositories;

import com.jawad.bank.notification.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByClientId(UUID clientId);

    List<Notification> findByTxId(UUID txId);
}
