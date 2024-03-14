package io.github.gms.common.config;

import io.github.gms.common.config.cache.ApiCacheKeyGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static io.github.gms.common.util.Constants.CACHE_API;
import static io.github.gms.common.util.Constants.CACHE_IP_RESTRICTION;
import static io.github.gms.common.util.Constants.CACHE_USER;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(name = "config.cache.redis.enabled", havingValue = "false")
public class CacheConfig implements CachingConfigurer {
	
	@Override
	@Bean
    public CacheManager cacheManager() {
		ConcurrentMapCacheManager manager = new ConcurrentMapCacheManager(
				CACHE_USER,
				"systemPropertyCache",
				CACHE_API,
				CACHE_IP_RESTRICTION);
		manager.setAllowNullValues(false);
		return manager;
    }
	
	@Bean
	@Primary
	@Override
	public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }
	
	@Bean("apiCacheKeyGenerator")
    public KeyGenerator apiCacheKeyGenerator() {
        return new ApiCacheKeyGenerator();
    }
}