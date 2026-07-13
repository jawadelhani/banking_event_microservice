package com.jawad.bank.notification.clients;

import com.jawad.bank.notification.dtos.ClientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "ACCOUNT-SERVICE")
public interface AccountClient {
    @GetMapping("/clients/me")
    ClientDto getCurrentClient(@RequestHeader("Authorization") String authorization);
}