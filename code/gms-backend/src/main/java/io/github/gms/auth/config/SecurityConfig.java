package io.github.gms.auth.config;

import io.github.gms.auth.GmsCsrfTokenRequestHandler;
import io.github.gms.auth.GmsSessionAuthenticationStrategy;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
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
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO;
import static org.springframework.http.HttpMethod.POST;

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

    @Override
    public Customizer<CsrfConfigurer<HttpSecurity>> csrfConfigurerCustomizer() {
        return csrf -> csrf.csrfTokenRepository(csrfTokenRepository())
                .sessionAuthenticationStrategy(new GmsSessionAuthenticationStrategy(csrfTokenRepository(), csrfTokenRequestHandler()))
                .ignoringRequestMatchers(
                        PathPatternRequestMatcher.withDefaults().matcher(POST, "/authenticate"),
                        PathPatternRequestMatcher.withDefaults().matcher(POST, "/logoutUser")
                )
                .ignoringRequestMatchers(FILTER_URL)
                .ignoringRequestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .csrfTokenRequestHandler(csrfTokenRequestHandler());
    }
}