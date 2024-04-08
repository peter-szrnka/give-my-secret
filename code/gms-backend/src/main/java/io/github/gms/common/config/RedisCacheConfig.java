package io.github.gms.common.config;

import io.github.gms.common.config.cache.ApiCacheKeyGenerator;
import io.github.gms.common.config.cache.KeycloakSsoKeyGenerator;
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
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

import static io.github.gms.common.util.Constants.CACHE_API;
import static io.github.gms.common.util.Constants.CACHE_API_GENERATOR;
import static io.github.gms.common.util.Constants.CACHE_GLOBAL_IP_RESTRICTION;
import static io.github.gms.common.util.Constants.CACHE_IP_RESTRICTION;
import static io.github.gms.common.util.Constants.CACHE_KEYCLOAK_SSO_GENERATOR;
import static io.github.gms.common.util.Constants.CACHE_SSO_USER;
import static io.github.gms.common.util.Constants.CACHE_SYSTEM_PROPERTY;
import static io.github.gms.common.util.Constants.CACHE_USER;
import static io.github.gms.common.util.Constants.TRUE;


/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(name = "config.cache.redis.enabled", havingValue = TRUE)
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
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, RedisCacheManagerBuilderCustomizer customizer) {
        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(connectionFactory)
                .withCacheConfiguration(CACHE_USER, minutesCacheConfig(10))
                .withCacheConfiguration(CACHE_SYSTEM_PROPERTY, minutesCacheConfig(10))
                .withCacheConfiguration(CACHE_API, minutesCacheConfig(10))
                .withCacheConfiguration(CACHE_GLOBAL_IP_RESTRICTION, minutesCacheConfig(10))
                .withCacheConfiguration(CACHE_IP_RESTRICTION, minutesCacheConfig(10))
                .withCacheConfiguration(CACHE_SSO_USER, minutesCacheConfig(5))
                .build();
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return RedisCacheManager.RedisCacheManagerBuilder::cacheDefaults;
    }

    @Bean
    @Primary
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    @Bean(CACHE_API_GENERATOR)
    public KeyGenerator apiCacheKeyGenerator() {
        return new ApiCacheKeyGenerator();
    }

    @Bean(CACHE_KEYCLOAK_SSO_GENERATOR)
    public KeyGenerator keycloakSsoKeyGenerator() {
        return new KeycloakSsoKeyGenerator();
    }

    private static RedisCacheConfiguration minutesCacheConfig(int minutes) {
        return RedisCacheConfiguration
                .defaultCacheConfig(Thread.currentThread().getContextClassLoader())
                .entryTtl(Duration.ofMinutes(minutes));
    }
}
