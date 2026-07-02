package com.jawad.bank.notification.dtos;

import com.jawad.bank.notification.entities.NotificationChannel;
import com.jawad.bank.notification.entities.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
    private UUID id;
    private UUID clientId;
    private UUID txId;
    private NotificationChannel channel;
    private String message;
    private NotificationStatus status;
    private LocalDateTime sentAt;
}
