package io.github.gms.auth.config;

import io.github.gms.auth.GmsCsrfTokenRequestHandler;
import io.github.gms.auth.GmsSessionAuthenticationStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile(CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO)
public class SecurityConfig extends AbstractSecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    @Primary
    public CsrfTokenRepository csrfTokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }

    @Bean
    @Primary
    public CsrfTokenRequestHandler csrfTokenRequestHandler() {
        return new GmsCsrfTokenRequestHandler();
    }

    @Bean
    @ConditionalOnProperty(name = "config.auth.csrf.enabled", havingValue = "true", matchIfMissing = true)
    public Customizer<CsrfConfigurer<HttpSecurity>> csrf() {
        return csrf -> csrf.csrfTokenRepository(csrfTokenRepository())
                .sessionAuthenticationStrategy(new GmsSessionAuthenticationStrategy(csrfTokenRepository(), csrfTokenRequestHandler()))
                .ignoringRequestMatchers(
                        new AntPathRequestMatcher("/authenticate", "POST"),
                        new AntPathRequestMatcher("/logoutUser", "POST")
                )
                .ignoringRequestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .csrfTokenRequestHandler(csrfTokenRequestHandler());
    }
}