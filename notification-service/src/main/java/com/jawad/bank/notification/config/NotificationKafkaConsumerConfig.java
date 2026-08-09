package com.jawad.bank.notification.config;

import com.jawad.bank.notification.events.CardSuggestionEvent;
import com.jawad.bank.notification.events.FraudAnalysisEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class NotificationKafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> baseProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-service");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    @Bean
    public ConsumerFactory<String, CardSuggestionEvent> cardSuggestionConsumerFactory() {
        JsonDeserializer<CardSuggestionEvent> deserializer = new JsonDeserializer<>(CardSuggestionEvent.class);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(baseProps(), new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CardSuggestionEvent> cardSuggestionKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CardSuggestionEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cardSuggestionConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, FraudAnalysisEvent> fraudAnalysisConsumerFactory() {
        JsonDeserializer<FraudAnalysisEvent> deserializer = new JsonDeserializer<>(FraudAnalysisEvent.class);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(baseProps(), new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FraudAnalysisEvent> fraudAnalysisKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FraudAnalysisEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(fraudAnalysisConsumerFactory());
        return factory;
    }
}
