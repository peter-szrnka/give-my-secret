package io.github.gms;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.gms.util.DemoDataManagerService;
import lombok.extern.slf4j.Slf4j;

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
}