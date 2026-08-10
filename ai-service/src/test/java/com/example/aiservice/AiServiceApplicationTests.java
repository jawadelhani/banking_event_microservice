package com.example.aiservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Disabled;

@SpringBootTest
@Disabled("Disabled in CI - requires external PostgreSQL/Kafka infrastructure")
class AiServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}