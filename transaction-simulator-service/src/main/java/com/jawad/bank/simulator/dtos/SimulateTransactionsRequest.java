package com.jawad.bank.simulator.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SimulateTransactionsRequest {

    @NotEmpty
    private List<UUID> accountIds;

    @NotNull
    @Min(1)
    @Max(100)
    private Integer count;
}
