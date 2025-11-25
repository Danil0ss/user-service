package com.example.demo;

import com.example.demo.config.RedisConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration; // НОВЫЙ ИМПОРТ
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Testcontainers
public abstract class BaseIntegrationTest {

    // ... (контейнер без изменений) ...
    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:16.1")
                    .withDatabaseName("test-db")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        // Настройка БД через TestContainers
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    // ====================================================================
    // КЛЮЧЕВОЕ ИЗМЕНЕНИЕ: Исключаем RedisConfig вложенным классом
    // ====================================================================
    @TestConfiguration // Класс, который применяется только в тестах
    static class TestApplicationConfiguration {

        // Переопределяем Бин CacheManager, чтобы он не требовал RedisConnectionFactory
        // Мы возвращаем No-Op CacheManager, который не делает ничего, но удовлетворяет UserService
        @org.springframework.context.annotation.Bean
        public org.springframework.cache.CacheManager cacheManager() {
            return new org.springframework.cache.support.NoOpCacheManager();
        }
    }
}