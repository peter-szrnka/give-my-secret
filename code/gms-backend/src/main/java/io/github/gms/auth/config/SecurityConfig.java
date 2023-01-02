package io.github.gms.auth.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
import io.github.gms.common.util.Constants;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@DependsOn(Constants.PASSWORD_ENCODER)
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = false, jsr250Enabled = false, prePostEnabled = true)
public class SecurityConfig {

	private static final String[] FILTER_URL = new String[] { "/", "/system/status", "/healthcheck", "/setup/**",
			"/login", "/authenticate", "/logoutUser", "/api/**", "/h2-console/**",
			"/gms-app/**", "/favicon.ico", "/assets/**", "/index.html**", "/*.js**", "/*.css**", "/*.json**",
			"/manifest.webmanifest" };

	@Value("${spring.h2.console.enabled:false}")
	private boolean h2ConsoleEnabled;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http,
			DaoAuthenticationProvider authenticationProvider,
			AuthenticationEntryPoint authenticationEntryPoint,
			SecureHeaderInitializerFilter secureHeaderInitializerFilter) throws Exception {
		http.cors().and().csrf().disable().and().exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
				.antMatchers(FILTER_URL).permitAll()
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll().anyRequest()
				.authenticated();

		http.authenticationProvider(authenticationProvider);

		http.addFilterBefore(secureHeaderInitializerFilter, UsernamePasswordAuthenticationFilter.class);

		http.formLogin().disable();
		http.httpBasic().disable();

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
