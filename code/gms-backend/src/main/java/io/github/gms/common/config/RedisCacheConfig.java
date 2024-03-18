package io.github.gms.common.config;

import io.github.gms.common.config.cache.ApiCacheKeyGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

import static io.github.gms.common.util.Constants.CACHE_API;
import static io.github.gms.common.util.Constants.CACHE_SYSTEM_PROPERTY;
import static io.github.gms.common.util.Constants.CACHE_USER;


/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(name = "config.cache.redis.enabled", havingValue = "true")
public class RedisCacheConfig {

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory(
            @Value("${config.cache.redis.host}") String host,
            @Value("${config.cache.redis.port}") Integer port
    ) {
        return new LettuceConnectionFactory(host, port);
    }

    @Primary
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setEnableDefaultSerializer(true);
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(connectionFactory)
                .withCacheConfiguration(CACHE_USER, default10MinuteCacheConfig())
                .withCacheConfiguration(CACHE_SYSTEM_PROPERTY, default10MinuteCacheConfig())
                .withCacheConfiguration(CACHE_API, default10MinuteCacheConfig())
                .build();
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration(CACHE_USER, default10MinuteCacheConfig())
                .withCacheConfiguration(CACHE_SYSTEM_PROPERTY, default10MinuteCacheConfig())
                .withCacheConfiguration(CACHE_API, default5MinuteCacheConfig());
    }

    @Bean
    @Primary
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    @Bean("apiCacheKeyGenerator")
    public KeyGenerator apiCacheKeyGenerator() {
        return new ApiCacheKeyGenerator();
    }

    private static RedisCacheConfiguration default5MinuteCacheConfig() {
        return RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5));
    }

    private static RedisCacheConfiguration default10MinuteCacheConfig() {
        return RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10));
    }
}
