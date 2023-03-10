package io.github.gms.common.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
		ConcurrentMapCacheManager manager = new ConcurrentMapCacheManager("systemStatusCache", "userCache", "systemPropertyCache");
		manager.setAllowNullValues(false);
		return manager;
    }
}
