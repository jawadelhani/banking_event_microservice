package com.example.aiservice.dtos;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRequest {
    private String model;
    private List<Message> messages;
    private Double temperature;

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Message {
        private String role;
        private String content;
    }
}