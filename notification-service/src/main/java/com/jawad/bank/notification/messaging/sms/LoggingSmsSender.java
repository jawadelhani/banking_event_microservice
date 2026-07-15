package com.jawad.bank.notification.messaging.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Placeholder SMS sender — logs instead of actually sending.
 * Replace with a real provider (e.g. Twilio, Vonage) by implementing
 * SmsSender and marking this class @Primary=false / removing it,
 * or swapping the @Service registration once you have provider credentials.
 */
@Slf4j
@Service
public class LoggingSmsSender implements SmsSender {

    @Override
    public void send(String phoneNumber, String message) {
        log.info("[SMS STUB] Would send to {}: {}", phoneNumber, message);
    }
}