package com.jawad.bank.notification.clients;

import com.jawad.bank.notification.dtos.ClientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "ACCOUNT-SERVICE")
public interface AccountClient {
    @GetMapping("/clients/me")
    ClientDto getCurrentClient(@RequestHeader("Authorization") String authorization);

    @GetMapping("/clients/{id}/internal")
    ClientDto getClientById(@PathVariable("id") UUID id);
}