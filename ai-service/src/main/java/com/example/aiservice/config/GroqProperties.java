package com.example.aiservice.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "groq")
public class GroqProperties {
    private String baseUrl;
    private String model;
    private String apiKey;
}