package com.jawad.bank.notification.dtos;

import com.jawad.bank.notification.entities.NotificationChannel;
import com.jawad.bank.notification.entities.NotificationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateNotificationRequest {

    @NotNull
    private NotificationChannel channel;

    @NotBlank
    private String message;

    @NotNull
    private NotificationStatus status;
}
