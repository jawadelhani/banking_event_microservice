package com.example.aiservice.dtos;

import lombok.Data;

@Data
public class CardSuggestionVerdict {
    private String suggestedCard;
    private String message;
}