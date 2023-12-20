package io.github.gms;

import org.springframework.boot.SpringApplication;
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
	RedisAutoConfiguration.class
})
public class GmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmsApplication.class, args);
	}
}