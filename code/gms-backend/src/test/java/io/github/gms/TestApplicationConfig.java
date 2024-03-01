package io.github.gms;

import io.github.gms.util.DemoDataManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Configuration
public class TestApplicationConfig {

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {	
		return args -> {
			log.info("Let's add some test data");
			
			DemoDataManagerService service = ctx.getBean(DemoDataManagerService.class);
			service.initTestData();
		};
	}

	// Default RestTemplate instance for integration tests
	@Bean("testRestTemplate")
	public RestTemplate testRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new GmsResponseErrorHandler());
		return restTemplate;
	}
}