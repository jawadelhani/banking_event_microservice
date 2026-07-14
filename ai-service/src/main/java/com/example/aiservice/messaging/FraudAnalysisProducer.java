package com.example.aiservice.messaging;


import com.example.aiservice.events.FraudAnalysisEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FraudAnalysisProducer {

    private final KafkaTemplate<String, FraudAnalysisEvent> kafkaTemplate;

    public void publish(FraudAnalysisEvent event) {
        kafkaTemplate.send("fraud-analysis", event);
    }
}