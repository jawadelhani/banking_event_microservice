package com.example.aiservice.messaging;

import com.example.aiservice.events.TransactionCreatedEvent;
import com.example.aiservice.services.ScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionConsumer {

    private final ScoringService scoringService;

    @KafkaListener(topics = "transaction-events", groupId = "ai-service")
    public void consume(TransactionCreatedEvent event) {
        scoringService.handle(event);
    }
}