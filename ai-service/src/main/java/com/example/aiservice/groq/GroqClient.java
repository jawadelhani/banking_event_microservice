package com.example.aiservice.groq;

import com.example.aiservice.config.GroqProperties;
import com.example.aiservice.dtos.CardSuggestionVerdict;
import com.example.aiservice.dtos.ChatRequest;
import com.example.aiservice.dtos.ChatResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroqClient {

    private final RestClient groqRestClient;
    private final GroqProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Pattern JSON_PATTERN = Pattern.compile("\\{.*\\}", Pattern.DOTALL);

    public CardSuggestionVerdict suggestCardTier(String spendingSummary) {
        log.info("Calling Groq at {} with model {}, key length={}",
                properties.getBaseUrl(), properties.getModel(),
                properties.getApiKey() == null ? "null" : properties.getApiKey().length());


        String systemPrompt = """
            You are an enthusiastic banking assistant for 'Al Barid Bank'. You suggest a card tier based on a client's
            recent weekly spending average.
            Rules:
            - weeklyAverage < 1000 -> STANDARD
            - weeklyAverage between 1000 and 10000 -> SILVER
            - weeklyAverage > 10000 -> GOLD
            
            You will receive a JSON summary of a client's weekly average spending.
            Respond with ONLY a JSON object, no explanation, no markdown, in this exact shape:
            {"suggestedCard": "STANDARD|SILVER|GOLD", "message": "<a beautifully formatted HTML message>"}
            
            The "message" field MUST be a beautifully organized HTML email body (using <br>, <strong>, etc.) including emojis.
            Make it look highly professional and warm. 
            Include the bank name 'Al Barid Bank' and an image tag with the logo: <img src="https://upload.wikimedia.org/wikipedia/commons/f/fd/Al_Barik_Bank_logo.png?utm_source=fr.wikipedia.org&utm_campaign=index&utm_content=original" width="150" alt="Al Barid Bank Logo"> at the top.
            Highlight the benefits of their new suggested card tier.
            Do NOT mention their specific spending amount or weekly average in the email.
            Do NOT include the <html> or <body> tags, just the inner HTML content.
            """;

        ChatRequest request = ChatRequest.builder()
                .model(properties.getModel())
                .temperature(0.0)
                .messages(List.of(
                        new ChatRequest.Message("system", systemPrompt),
                        new ChatRequest.Message("user", spendingSummary)
                ))
                .build();

        ChatResponse response = groqRestClient.post()
                .uri("/chat/completions")
                .body(request)
                .retrieve()
                .body(ChatResponse.class);

        String raw = response.getChoices().get(0).getMessage().getContent();

        return parseVerdict(raw);
    }

    private CardSuggestionVerdict parseVerdict(String raw) {
        try {
            Matcher matcher = JSON_PATTERN.matcher(raw);
            String json = matcher.find() ? matcher.group() : raw;
            return objectMapper.readValue(json, CardSuggestionVerdict.class);
        } catch (Exception e) {
            log.warn("Failed to parse Groq response, raw content: {}", raw, e);
            throw new IllegalStateException("Unparseable Groq response", e);
        }
    }
}
