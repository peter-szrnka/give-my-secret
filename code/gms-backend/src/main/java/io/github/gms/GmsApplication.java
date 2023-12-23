package io.github.gms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.data.redis.RedisHealthContributorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.ldap.LdapHealthContributorAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.ldap.LdapRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;

/**
 * Main Spring Boot Application
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
@SpringBootApplication(exclude = {
	LdapRepositoriesAutoConfiguration.class,
	JacksonAutoConfiguration.class,
	LdapRepositoriesAutoConfiguration.class,
	RedisAutoConfiguration.class,
	// Actuator auto configurations
	LdapHealthContributorAutoConfiguration.class,
	RedisHealthContributorAutoConfiguration.class
})
public class GmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmsApplication.class, args);
	}
}