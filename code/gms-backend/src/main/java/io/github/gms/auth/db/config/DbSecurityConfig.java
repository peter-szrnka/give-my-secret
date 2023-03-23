package io.github.gms.auth.db.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_DB;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Configuration
@Profile(CONFIG_AUTH_TYPE_DB)
public class DbSecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
