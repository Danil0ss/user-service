package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration; // НОВЫЙ ИМПОРТ


@SpringBootTest(
		properties = {
				// КЛЮЧЕВОЕ ИЗМЕНЕНИЕ: РАЗРЕШАЕМ ПЕРЕОПРЕДЕЛЕНИЕ БИНОВ (для Unit Test это допустимо)
				"spring.main.allow-bean-definition-overriding=true", // <-- ЭТО РЕШАЕТ ПРОБЛЕМУ

				// Отключение Liquibase и кэша
				"spring.liquibase.enabled=false",
				"spring.cache.type=none",
				"spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1" // Используем H2-заглушку
		}
)
class DemoApplicationTests {

	@Test
	void contextLoads() {
		// Тест проходит, если IoC-контейнер стартует.
	}

	// Вложенный @Configuration для переопределения CacheManager
	@Configuration
	static class TestApplicationConfiguration {

		// Переопределяем CacheManager для Unit Test.
		// @Bean cacheManager теперь переопределит RedisConfig.cacheManager
		@org.springframework.context.annotation.Bean
		public org.springframework.cache.CacheManager cacheManager() {
			return new org.springframework.cache.support.NoOpCacheManager();
		}
	}
}