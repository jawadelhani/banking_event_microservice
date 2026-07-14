package com.example.aiservice.dtos;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private List<Choice> choices;

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class Choice {
        private Message message;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class Message {
        private String content;
    }
}