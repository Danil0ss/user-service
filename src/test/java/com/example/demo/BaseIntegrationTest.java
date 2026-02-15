package com.example.demo;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public abstract class BaseIntegrationTest {


    static {

        System.setProperty("testcontainers.docker.client.strategy", "org.testcontainers.dockerclient.UnixSocketClientProviderStrategy");

        System.setProperty("docker.host", "unix:///var/run/docker.sock");
    }

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test-db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.liquibase.enabled", () -> "true");
        registry.add("spring.cache.type", () -> "none");
        registry.add("spring.data.redis.repositories.enabled", () -> "false");


        registry.add("jwt.secret", () -> "dGVzdC1zZWNyZXQta2V5LWZvci1pbnRlZ3JhdGlvbi10ZXN0cy1tdXN0LWJlLWxvbmc=");
        registry.add("jwt.access-expiration", () -> "3600000");
        registry.add("jwt.refresh-expiration", () -> "3600000");
    }
}