package io.github.gms.common.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.github.gms.common.config.cache.ApiCacheKeyGenerator;
import io.github.gms.common.util.Constants;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {
	
	@Override
	@Bean
    public CacheManager cacheManager() {
		ConcurrentMapCacheManager manager = new ConcurrentMapCacheManager("systemStatusCache", Constants.CACHE_USER, "systemPropertyCache", Constants.CACHE_API);
		manager.setAllowNullValues(false);
		return manager;
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
}