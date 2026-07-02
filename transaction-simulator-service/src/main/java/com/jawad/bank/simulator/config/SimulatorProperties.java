package com.jawad.bank.simulator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@ConfigurationProperties(prefix = "simulator")
@Getter
@Setter
public class SimulatorProperties {

    private BigDecimal minAmount = BigDecimal.valueOf(10);
    private BigDecimal maxAmount = BigDecimal.valueOf(10000);
}
