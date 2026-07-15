package com.jawad.bank.notification.messaging.mail;


public interface EmailSender {
    void send(String to, String subject, String body);
}