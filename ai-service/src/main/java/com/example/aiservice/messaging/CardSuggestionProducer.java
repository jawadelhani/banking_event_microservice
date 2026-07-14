package com.example.aiservice.messaging;

import com.example.aiservice.events.CardSuggestionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardSuggestionProducer {

    private final KafkaTemplate<String, CardSuggestionEvent> kafkaTemplate;

    public void publish(CardSuggestionEvent event) {
        kafkaTemplate.send("card-suggestions", event);
    }
}
