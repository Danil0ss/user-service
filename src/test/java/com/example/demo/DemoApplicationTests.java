package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;


@SpringBootTest(
		properties = {

				"spring.main.allow-bean-definition-overriding=true",
				"spring.liquibase.enabled=false",
				"spring.cache.type=none",
				"spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"
		}
)
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}

	@Configuration
	static class TestApplicationConfiguration {

		@org.springframework.context.annotation.Bean
		public org.springframework.cache.CacheManager cacheManager() {
			return new org.springframework.cache.support.NoOpCacheManager();
		}
	}
}