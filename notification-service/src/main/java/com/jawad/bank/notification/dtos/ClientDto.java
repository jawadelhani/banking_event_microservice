package com.jawad.bank.notification.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class ClientDto {

    private UUID id;

    private String fullName;

    private String email;

    private String phone;

    private Boolean allowNotifications;
}
