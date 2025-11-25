package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// ====================================================================================================
// Изолируем Unit Test от ВСЕХ зависимостей DB/JPA/Liquibase/Cache
// ====================================================================================================

@SpringBootTest(properties = {
		"spring.liquibase.enabled=false",
		"spring.jpa.hibernate.ddl-auto=none",

		"spring.autoconfigure.exclude=" +
				"org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration," +
				"org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
				"org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})
class DemoApplicationTests {

	@Test
	void contextLoads() {

	}
}