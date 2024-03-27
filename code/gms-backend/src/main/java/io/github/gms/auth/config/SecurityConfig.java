package io.github.gms.auth.config;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO;
import static io.github.gms.common.util.Constants.PASSWORD_ENCODER;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@DependsOn(PASSWORD_ENCODER)
@Profile(CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO)
public class SecurityConfig extends AbstractSecurityConfig {

	@Bean
	public CodeVerifier codeVerifier() {
		TimeProvider timeProvider = new SystemTimeProvider();
		CodeGenerator codeGenerator = new DefaultCodeGenerator();
		return new DefaultCodeVerifier(codeGenerator, timeProvider);
	}
}