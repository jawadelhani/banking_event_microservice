package com.jawad.bank.notification.controllers;

import com.jawad.bank.notification.dtos.CreateNotificationRequest;
import com.jawad.bank.notification.dtos.NotificationDto;
import com.jawad.bank.notification.dtos.UpdateNotificationRequest;
import com.jawad.bank.notification.services.NotificationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public Iterable<NotificationDto> getAllNotifications() {
        return notificationService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationDto> getNotification(@PathVariable UUID id) {
        return notificationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{clientId}")
    public Iterable<NotificationDto> getByClient(@PathVariable UUID clientId) {
        return notificationService.findByClientId(clientId);
    }

    @GetMapping("/transaction/{txId}")
    public Iterable<NotificationDto> getByTransaction(@PathVariable UUID txId) {
        return notificationService.findByTxId(txId);
    }

    @PostMapping
    public ResponseEntity<NotificationDto> createNotification(
            @Valid @RequestBody CreateNotificationRequest request,
            UriComponentsBuilder uriBuilder) {
        NotificationDto dto = notificationService.create(request);
        var uri = uriBuilder.path("/notifications/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationDto> updateNotification(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateNotificationRequest request) {
        return notificationService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id) {
        if (!notificationService.delete(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
