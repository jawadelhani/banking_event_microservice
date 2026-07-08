package com.jawad.bank.simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient

public class TransactionSimulatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionSimulatorApplication.class, args);
    }
}
