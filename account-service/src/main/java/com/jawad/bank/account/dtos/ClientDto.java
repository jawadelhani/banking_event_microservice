package com.jawad.bank.account.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ClientDto {
    private UUID id;
    private String cin;
    private String fullName;
    private String email;
    private String phone;
    private Boolean allowNotifications;
    private BigDecimal monthlyIncome;
    private LocalDateTime createdAt;
}
