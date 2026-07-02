package com.jawad.bank.notification.mappers;

import com.jawad.bank.notification.dtos.CreateNotificationRequest;
import com.jawad.bank.notification.dtos.NotificationDto;
import com.jawad.bank.notification.dtos.UpdateNotificationRequest;
import com.jawad.bank.notification.entities.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationDto toDto(Notification notification);

    Notification toEntity(CreateNotificationRequest request);

    void updateNotification(UpdateNotificationRequest request, @MappingTarget Notification notification);
}
