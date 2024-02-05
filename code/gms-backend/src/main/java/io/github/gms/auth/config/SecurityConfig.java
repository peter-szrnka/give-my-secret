package io.github.gms.auth.config;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import io.github.gms.common.filter.SecureHeaderInitializerFilter;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static io.github.gms.common.util.Constants.PASSWORD_ENCODER;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@DependsOn(PASSWORD_ENCODER)
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private static final String[] FILTER_URL = new String[] { "/", "/system/status", "/healthcheck", "/setup/**",
			"/login", "/authenticate", "/verify", "/logoutUser", "/api/**", "/info/me", "/actuator/**",
			"/gms-app/**", "/favicon.ico", "/assets/**", "/index.html**", "/*.js**", "/*.css**", "/*.json**",
			"/manifest.webmanifest", "/reset_password" };

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http,
			DaoAuthenticationProvider authenticationProvider,
			AuthenticationEntryPoint authenticationEntryPoint,
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

		http.authenticationProvider(authenticationProvider);
		http.addFilterBefore(secureHeaderInitializerFilter, UsernamePasswordAuthenticationFilter.class);
		http.formLogin(FormLoginConfigurer<HttpSecurity>::disable);
		http.httpBasic(HttpBasicConfigurer<HttpSecurity>::disable);

		return http.build();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder);

		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:4200"));
		configuration.setAllowedMethods(List.of("*"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public CodeVerifier codeVerifier() {
		TimeProvider timeProvider = new SystemTimeProvider();
		CodeGenerator codeGenerator = new DefaultCodeGenerator();
		return new DefaultCodeVerifier(codeGenerator, timeProvider);
	}
}