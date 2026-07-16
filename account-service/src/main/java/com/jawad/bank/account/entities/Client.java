package com.jawad.bank.account.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String cin;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "keycloak_user_id", unique = true)
    private String keycloakUserId;

    @Column(nullable = false)
    private String email;

    private String phone;

    @Column(name = "monthly_income")
    private BigDecimal monthlyIncome;

    @Column(name = "allow_notifications", nullable = false)
    @Builder.Default
    private Boolean allowNotifications = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
