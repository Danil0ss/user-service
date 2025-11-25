package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration; // НОВЫЙ ИМПОРТ
import org.springframework.context.annotation.Import;


@SpringBootTest
class DemoApplicationTests {

	@Test
	void contextLoads() {
		// Проверка, что IoC-контейнер стартует.
	}

	// ====================================================================
	// КЛЮЧЕВОЕ ИЗМЕНЕНИЕ: Переопределяем бины только для тестов
	// ====================================================================
	@TestConfiguration
	static class TestApplicationConfiguration {

		// 1. Переопределяем CacheManager (для UserService)
		@org.springframework.context.annotation.Bean
		public org.springframework.cache.CacheManager cacheManager() {
			return new org.springframework.cache.support.NoOpCacheManager();
		}

		// 2. Поскольку это Unit Test, мы должны отключить DB/JPA/Liquibase.
		// Простейший способ — использовать @TestPropertySource, если нет, то:
		// @org.springframework.context.annotation.Bean
		// public javax.sql.DataSource dataSource() { return Mockito.mock(DataSource.class); }
	}
}