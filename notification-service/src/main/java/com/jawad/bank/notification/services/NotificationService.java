package com.jawad.bank.notification.services;

import com.jawad.bank.notification.dtos.CreateNotificationRequest;
import com.jawad.bank.notification.dtos.NotificationDto;
import com.jawad.bank.notification.dtos.UpdateNotificationRequest;
import com.jawad.bank.notification.entities.Notification;
import com.jawad.bank.notification.mappers.NotificationMapper;
import com.jawad.bank.notification.repositories.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public List<NotificationDto> findAll() {
        return notificationRepository.findAll()
                .stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    public List<NotificationDto> findByClientId(UUID clientId) {
        return notificationRepository.findByClientId(clientId)
                .stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    public List<NotificationDto> findByTxId(UUID txId) {
        return notificationRepository.findByTxId(txId)
                .stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    public Optional<NotificationDto> findById(UUID id) {
        return notificationRepository.findById(id).map(notificationMapper::toDto);
    }

    /**
     * Stores a notification record only — no email/SMS is sent yet.
     * Actual delivery will be wired in a later sprint (Kafka + external providers).
     */
    @Transactional
    public NotificationDto create(CreateNotificationRequest request) {
        Notification notification = notificationMapper.toEntity(request);
        return notificationMapper.toDto(notificationRepository.save(notification));
    }

    @Transactional
    public Optional<NotificationDto> update(UUID id, UpdateNotificationRequest request) {
        return notificationRepository.findById(id)
                .map(notification -> {
                    notificationMapper.updateNotification(request, notification);
                    return notificationMapper.toDto(notificationRepository.save(notification));
                });
    }

    @Transactional
    public boolean delete(UUID id) {
        if (!notificationRepository.existsById(id)) {
            return false;
        }
        notificationRepository.deleteById(id);
        return true;
    }
}
