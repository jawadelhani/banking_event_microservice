package com.example.aiservice.dtos;

import lombok.Data;

@Data
public class GroqVerdict {
    private double anomalyScore; // 0.0 - 1.0
    private boolean suspicious;
    private String reason;
}
