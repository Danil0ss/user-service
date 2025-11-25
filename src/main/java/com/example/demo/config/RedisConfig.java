package com.example.demo.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    // Основной бин — только когда Redis реально подключён
    @Bean
    @Primary
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
            name = "spring.cache.type",
            havingValue = "redis",
            matchIfMissing = true
    )
    public CacheManager redisCacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues();

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }

    // Fallback — в тестах, когда Redis отключён или spring.cache.type=none
    @Bean
    @Primary
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
            name = "spring.cache.type",
            havingValue = "redis",
            matchIfMissing = false
    )
    public CacheManager simpleCacheManager() {
        return new org.springframework.cache.concurrent.ConcurrentMapCacheManager();
    }
}