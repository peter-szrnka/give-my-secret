package io.github.gms.auth.config;

import io.github.gms.common.filter.RequestInitializationFilter;
import io.github.gms.common.filter.SecureHeaderInitializerFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
public abstract class AbstractSecurityConfig {

    private static final String[] FILTER_URL = new String[]{"/", "/system/status", "/healthcheck", "/setup/**",
            "/login", "/authenticate", "/verify", "/logoutUser", "/api/**", "/info/me", "/actuator/**",
            "/gms-app/**", "/favicon.ico", "/assets/**", "/index.html**", "/*.js**", "/*.css**", "/*.json**",
            "/manifest.webmanifest", "/reset_password", "/error_codes"};

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationEntryPoint authenticationEntryPoint,
                                           RequestInitializationFilter requestInitializationFilter,
                                           SecureHeaderInitializerFilter secureHeaderInitializerFilter) throws Exception {
        http
                .cors(cors -> Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(e -> e.authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeHttpRequest ->
                        authorizeHttpRequest.requestMatchers(FILTER_URL).permitAll()
                                .requestMatchers(FILTER_URL).permitAll()
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll().anyRequest()
                                .authenticated()
                );

        http.addFilterBefore(secureHeaderInitializerFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(requestInitializationFilter, SecureHeaderInitializerFilter.class);
        http.formLogin(FormLoginConfigurer<HttpSecurity>::disable);
        http.httpBasic(HttpBasicConfigurer<HttpSecurity>::disable);

        return http.build();
    }

    @Bean
    @Profile("dev")
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        log.info("CORS configuration is enabled for development profile");
        return source;
    }
}
