package com.jawad.bank.notification.mappers;

import com.jawad.bank.notification.dtos.CreateNotificationRequest;
import com.jawad.bank.notification.dtos.NotificationDto;
import com.jawad.bank.notification.dtos.UpdateNotificationRequest;
import com.jawad.bank.notification.entities.Notification;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-02T12:08:14+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationDto toDto(Notification notification) {
        if ( notification == null ) {
            return null;
        }

        NotificationDto notificationDto = new NotificationDto();

        return notificationDto;
    }

    @Override
    public Notification toEntity(CreateNotificationRequest request) {
        if ( request == null ) {
            return null;
        }

        Notification.NotificationBuilder notification = Notification.builder();

        notification.clientId( request.getClientId() );
        notification.txId( request.getTxId() );
        notification.channel( request.getChannel() );
        notification.message( request.getMessage() );
        notification.status( request.getStatus() );

        return notification.build();
    }

    @Override
    public void updateNotification(UpdateNotificationRequest request, Notification notification) {
        if ( request == null ) {
            return;
        }

        notification.setChannel( request.getChannel() );
        notification.setMessage( request.getMessage() );
        notification.setStatus( request.getStatus() );
    }
}
