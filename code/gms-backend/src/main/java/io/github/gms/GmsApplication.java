package io.github.gms;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.ldap.LdapRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import io.github.gms.common.util.DemoDataProviderService;
import lombok.extern.slf4j.Slf4j;

/**
 * Main Spring Boot Application
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@SpringBootApplication(exclude = {
	LdapRepositoriesAutoConfiguration.class,
	JacksonAutoConfiguration.class
})
public class GmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmsApplication.class, args);
	}

	@Bean
	@ConditionalOnProperty(value = "config.enable.test.data", havingValue = "true")
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {	
		return args -> {
			log.info("Let's add some test data");
			
			DemoDataProviderService service = ctx.getBean(DemoDataProviderService.class);
			service.initTestData();
		};
	}
}