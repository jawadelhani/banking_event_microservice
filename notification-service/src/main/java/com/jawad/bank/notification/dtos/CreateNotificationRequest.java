package com.jawad.bank.notification.dtos;

import com.jawad.bank.notification.entities.NotificationChannel;
import com.jawad.bank.notification.entities.NotificationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateNotificationRequest {

    @NotNull
    private UUID clientId;

    @NotNull
    private UUID txId;

    @NotNull
    private NotificationChannel channel;

    @NotBlank
    private String message;

    @NotNull
    private NotificationStatus status;
}
