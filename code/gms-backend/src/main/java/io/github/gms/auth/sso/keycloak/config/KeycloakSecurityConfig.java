package io.github.gms.auth.sso.keycloak.config;

import io.github.gms.auth.config.AbstractSecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_KEYCLOAK_SSO;
import static io.github.gms.common.util.Constants.PASSWORD_ENCODER;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile(CONFIG_AUTH_TYPE_KEYCLOAK_SSO)
public class KeycloakSecurityConfig extends AbstractSecurityConfig {

    @Bean(PASSWORD_ENCODER)
    public PasswordEncoder passwordEncoder() {
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
