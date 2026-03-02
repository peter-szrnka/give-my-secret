package io.github.gms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Configuration
@EnableTransactionManagement
public class TestApplicationConfig {

	// Default RestTemplate instance for integration tests
	@Bean("testRestTemplate")
	public RestTemplate testRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new GmsResponseErrorHandler());
		return restTemplate;
	}

    @Bean
    public Map<String, Long> entityMap() {
        return new ConcurrentHashMap<>();
    }
}