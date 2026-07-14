package com.jawad.bank.simulator.messaging;

import com.jawad.bank.simulator.events.TransactionCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionProducer {

    private final KafkaTemplate<String, TransactionCreatedEvent> kafkaTemplate;

    public void publish(TransactionCreatedEvent event) {

        kafkaTemplate.send(
                "transaction-events",
                event
        );

    }

}