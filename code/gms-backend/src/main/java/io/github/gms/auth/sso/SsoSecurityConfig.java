package io.github.gms.auth.sso;

import io.github.gms.auth.config.BaseSecurityConfig;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static io.github.gms.common.util.Constants.PASSWORD_ENCODER;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Configuration
@Profile("sso")
@EnableWebSecurity
@EnableMethodSecurity
public class SsoSecurityConfig extends BaseSecurityConfig {

    private static final String[] FILTER_URL = new String[] { "/system/status", "/healthcheck", "/setup/**",
            "/logoutUser", "/api/**", "/info/me", "/actuator/**",
            "/gms-app/**", "/favicon.ico", "/assets/**", "/index.html**", "/*.js**", "/*.css**", "/*.json**",
            "/manifest.webmanifest" };

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .cors(cors -> Customizer.withDefaults())
                //.oauth2ResourceServer((oauth2) -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(customJwtConverter());
                .oauth2Client(client -> Customizer.withDefaults())
                .oauth2Login(login -> login.tokenEndpoint(e -> Customizer.withDefaults()).userInfoEndpoint(e -> Customizer.withDefaults()))
        ;

        http
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.ALWAYS));

        http
                .authorizeHttpRequests(authorizeHttpRequest ->
                        authorizeHttpRequest
                                .requestMatchers(FILTER_URL).permitAll()
                                //.requestMatchers("/unauthenticated", "/oauth2/**", "/login/**").permitAll()
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                .anyRequest()
                                .authenticated()
                )
                //.logout(logout ->
                 //       logout.logoutSuccessUrl("http://localhost:7000/realms/gms-client/protocol/openid-connect/logout?redirect_uri=http://localhost:4200/"))
        ;

        return http.build();
    }

    @Bean(PASSWORD_ENCODER)
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
