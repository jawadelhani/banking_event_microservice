package com.jawad.bank.simulator.clients;

import com.jawad.bank.simulator.dtos.AccountDto;
import com.jawad.bank.simulator.dtos.ClientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "ACCOUNT-SERVICE")
public interface AccountClient {

    @GetMapping("/clients/me")
    ClientDto getCurrentClient(@RequestHeader("Authorization") String authorization);

    @GetMapping("/accounts/me")
    List<AccountDto> getCurrentAccounts(@RequestHeader("Authorization") String authorization);
}