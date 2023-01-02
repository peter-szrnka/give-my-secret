package io.github.gms.auth.ldap.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.github.gms.common.util.Constants;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Configuration
@Profile(value = { Constants.CONFIG_AUTH_TYPE_LDAP, Constants.CONFIG_AUTH_TYPE_LDAP_TEST })
public class LdapSecurityConfig {

	@Bean(Constants.PASSWORD_ENCODER)
	@ConditionalOnProperty(name = Constants.CONFIG_LDAP_PASSWORD_ENCODER, havingValue = "CRYPT", matchIfMissing = true)
	public PasswordEncoder bcryptPasswordEncoder() {
		final BCryptPasswordEncoder crypt = new BCryptPasswordEncoder();
		return new PasswordEncoder() {
			@Override
			public String encode(CharSequence rawPassword) {
				return Constants.LDAP_CRYPT_PREFIX + crypt.encode(rawPassword);
			}

			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword) {
				return crypt.matches(rawPassword, encodedPassword.substring(7));
			}
		};
	}
	
	@Bean(Constants.PASSWORD_ENCODER)
	@ConditionalOnProperty(name = Constants.CONFIG_LDAP_PASSWORD_ENCODER, havingValue = "PLAIN_TEXT")
	public static PasswordEncoder plainTextEncoder() {
		return new PasswordEncoder() {
			
			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword) {
				return rawPassword.toString().equals(encodedPassword);
			}
			
			@Override
			public String encode(CharSequence rawPassword) {
				return rawPassword.toString();
			}
		};
	}
}
