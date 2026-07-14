package com.jawad.bank.simulator.clients;

import com.jawad.bank.simulator.dtos.AccountDto;
import com.jawad.bank.simulator.dtos.BalanceAdjustmentRequest;
import com.jawad.bank.simulator.dtos.ClientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "ACCOUNT-SERVICE")
public interface AccountClient {

    @GetMapping("/clients/me")
    ClientDto getCurrentClient(@RequestHeader("Authorization") String authorization);

    @GetMapping("/accounts/me")
    List<AccountDto> getCurrentAccounts(@RequestHeader("Authorization") String authorization);

    @PutMapping("/accounts/{id}/balance")
    AccountDto adjustBalance(@PathVariable("id") UUID id,
                             @RequestBody BalanceAdjustmentRequest request,
                             @RequestHeader("Authorization") String authorization);
}
