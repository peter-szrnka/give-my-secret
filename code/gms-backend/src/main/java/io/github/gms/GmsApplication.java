package io.github.gms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.ldap.autoconfigure.DataLdapRepositoriesAutoConfiguration;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.boot.data.redis.autoconfigure.health.DataRedisHealthContributorAutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.ldap.autoconfigure.health.LdapHealthContributorAutoConfiguration;

/**
 * Main Spring Boot Application
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
@SpringBootApplication(exclude = {
    DataLdapRepositoriesAutoConfiguration.class,
	JacksonAutoConfiguration.class,
	DataRedisAutoConfiguration.class,
	// Actuator auto configurations
	LdapHealthContributorAutoConfiguration.class,
    DataRedisHealthContributorAutoConfiguration.class
})
@ImportAutoConfiguration(FlywayAutoConfiguration.class)
public class GmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmsApplication.class, args);
	}
}