package com.jawad.bank.agency.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class ClientDto {

    private UUID id;

    private String fullName;

    private String email;
}
