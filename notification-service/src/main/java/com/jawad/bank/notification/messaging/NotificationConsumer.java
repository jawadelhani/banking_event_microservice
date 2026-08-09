package com.jawad.bank.notification.messaging;

import com.jawad.bank.notification.clients.AccountClient;
import com.jawad.bank.notification.dtos.ClientDto;
import com.jawad.bank.notification.entities.Notification;
import com.jawad.bank.notification.entities.NotificationChannel;
import com.jawad.bank.notification.entities.NotificationStatus;
import com.jawad.bank.notification.events.CardSuggestionEvent;
import com.jawad.bank.notification.messaging.mail.EmailSender;
import com.jawad.bank.notification.messaging.sms.SmsSender;
import com.jawad.bank.notification.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final AccountClient accountClient;
    private final EmailSender emailSender;
    private final SmsSender smsSender;
    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = "card-suggestions", containerFactory = "cardSuggestionKafkaListenerContainerFactory")
    public void consume(CardSuggestionEvent event) {
        ClientDto client;
        try {
            client = accountClient.getClientById(event.getClientId());
        } catch (Exception e) {
            log.warn("Could not fetch client {} for notification, skipping", event.getClientId(), e);
            return;
        }

        if (client.getAllowNotifications() == null || !client.getAllowNotifications()) {
            log.info("Client {} has notifications disabled, skipping", client.getId());
            return;
        }

        String message = event.getMessage();

        if (client.getEmail() != null && !client.getEmail().isBlank()) {
            sendAndRecord(event.getClientId(), event.getTransactionId(), NotificationChannel.EMAIL, message,
                    () -> emailSender.send(client.getEmail(), "Your card suggestion", message));
        }

        if (client.getPhone() != null && !client.getPhone().isBlank()) {
            sendAndRecord(event.getClientId(), event.getTransactionId(), NotificationChannel.SMS, message,
                    () -> smsSender.send(client.getPhone(), message));
        }
    }

    @KafkaListener(topics = "fraud-analysis", containerFactory = "fraudAnalysisKafkaListenerContainerFactory")
    public void consumeFraud(com.jawad.bank.notification.events.FraudAnalysisEvent event) {
        if (!event.isSuspicious()) return;

        ClientDto client;
        try {
            client = accountClient.getClientById(event.getClientId());
        } catch (Exception e) {
            log.warn("Could not fetch client {} for fraud notification, skipping", event.getClientId(), e);
            return;
        }

        String message = "<h2 style='color:red;'>🚨 Fraud Alert: Suspicious Transaction Detected</h2>" +
                "<p>Dear " + client.getFullName() + ",</p>" +
                "<p>We detected a highly unusual transaction of <strong>" + event.getAmount() + " MAD</strong> on your account.</p>" +
                "<p><strong>Reason:</strong> " + event.getReason() + "</p>" +
                "<p>If this was not you, please contact Al Barid Bank immediately.</p>";

        if (client.getEmail() != null && !client.getEmail().isBlank()) {
            sendAndRecord(event.getClientId(), event.getTransactionId(), NotificationChannel.EMAIL, message,
                    () -> emailSender.send(client.getEmail(), "🚨 URGENT: Fraud Alert on your Account", message));
        }
    }

    private void sendAndRecord(java.util.UUID clientId, java.util.UUID txId, NotificationChannel channel,
                               String message, Runnable action) {
        NotificationStatus status;
        try {
            action.run();
            status = NotificationStatus.SENT;
        } catch (Exception e) {
            log.warn("Failed to send {} notification for client {}", channel, clientId, e);
            status = NotificationStatus.FAILED;
        }

        Notification notification = Notification.builder()
                .clientId(clientId)
                .txId(txId)
                .channel(channel)
                .message(message)
                .status(status)
                .build();

        notificationRepository.save(notification);
    }
}