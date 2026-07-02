package com.jawad.bank.notification.services;

import com.jawad.bank.notification.dtos.CreateNotificationRequest;
import com.jawad.bank.notification.dtos.NotificationDto;
import com.jawad.bank.notification.dtos.UpdateNotificationRequest;
import com.jawad.bank.notification.entities.Notification;
import com.jawad.bank.notification.mappers.NotificationMapper;
import com.jawad.bank.notification.repositories.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void findAll() {
        Notification notification = new Notification();
        NotificationDto dto = new NotificationDto();

        when(notificationRepository.findAll()).thenReturn(List.of(notification));
        when(notificationMapper.toDto(notification)).thenReturn(dto);

        List<NotificationDto> result = notificationService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void findById() {
        UUID id = UUID.randomUUID();
        Notification notification = new Notification();
        NotificationDto dto = new NotificationDto();

        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));
        when(notificationMapper.toDto(notification)).thenReturn(dto);

        Optional<NotificationDto> result = notificationService.findById(id);

        assertTrue(result.isPresent());
    }

    @Test
    void create() {
        CreateNotificationRequest request = new CreateNotificationRequest();
        Notification notification = new Notification();
        NotificationDto dto = new NotificationDto();

        when(notificationMapper.toEntity(request)).thenReturn(notification);
        when(notificationRepository.save(notification)).thenReturn(notification);
        when(notificationMapper.toDto(notification)).thenReturn(dto);

        NotificationDto result = notificationService.create(request);

        assertNotNull(result);
    }

    @Test
    void update() {
        UUID id = UUID.randomUUID();
        Notification notification = new Notification();
        UpdateNotificationRequest request = new UpdateNotificationRequest();
        NotificationDto dto = new NotificationDto();

        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(notification)).thenReturn(notification);
        when(notificationMapper.toDto(notification)).thenReturn(dto);

        Optional<NotificationDto> result = notificationService.update(id, request);

        assertTrue(result.isPresent());
    }

    @Test
    void delete() {
        UUID existingId = UUID.randomUUID();
        when(notificationRepository.existsById(existingId)).thenReturn(true);

        assertTrue(notificationService.delete(existingId));

        UUID missingId = UUID.randomUUID();
        when(notificationRepository.existsById(missingId)).thenReturn(false);

        assertFalse(notificationService.delete(missingId));
    }
}