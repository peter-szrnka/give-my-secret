package io.github.gms.auth.config;

import static io.github.gms.common.util.Constants.PASSWORD_ENCODER;

import java.util.List;

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
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
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

import io.github.gms.common.filter.SecureHeaderInitializerFilter;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@DependsOn(PASSWORD_ENCODER)
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	private static final String[] FILTER_URL = new String[] { "/", "/system/status", "/healthcheck", "/setup/**",
			"/login", "/authenticate", "/logoutUser", "/api/**",
			"/gms-app/**", "/favicon.ico", "/assets/**", "/index.html**", "/*.js**", "/*.css**", "/*.json**",
			"/manifest.webmanifest" };

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http,
			DaoAuthenticationProvider authenticationProvider,
			AuthenticationEntryPoint authenticationEntryPoint,
			SecureHeaderInitializerFilter secureHeaderInitializerFilter) throws Exception {
		http
			.cors(cors -> Customizer.withDefaults())
			.csrf(CsrfConfigurer::disable)
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

}
