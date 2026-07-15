package com.jawad.bank.notification.messaging.sms;

public interface SmsSender {
    void send(String phoneNumber, String message);
}
 