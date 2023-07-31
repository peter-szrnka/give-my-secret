
package io.github.gms.secure.service.impl;

import static io.github.gms.common.util.Constants.OK;
import static io.github.gms.common.util.Constants.SELECTED_AUTH;
import static io.github.gms.common.util.Constants.SELECTED_AUTH_DB;
import static io.github.gms.common.util.Constants.SELECTED_AUTH_LDAP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.common.event.RefreshCacheEvent;
import io.github.gms.secure.repository.UserRepository;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class SystemServiceImplTest extends AbstractLoggingUnitTest {

	private Clock clock = mock(Clock.class);
	private UserRepository userRepository;
	private Environment env;
	private BuildProperties buildProperties;
	private SystemServiceImpl service;

	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		((Logger) LoggerFactory.getLogger(SystemServiceImpl.class)).addAppender(logAppender);

		userRepository = mock(UserRepository.class, Mockito.RETURNS_SMART_NULLS);
		env = mock(Environment.class, Mockito.RETURNS_SMART_NULLS);
		buildProperties = mock(BuildProperties.class);

		service = new SystemServiceImpl(userRepository, clock, env);
		service.setBuildProperties(buildProperties);
	}
	
	@ParameterizedTest
	@ValueSource(strings = { "NEED_SETUP", OK })
	void shouldReturnSystemStatus(String mockResponse) {
		// arrange
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneId.of("Europe/Budapest"));
		when(buildProperties.getTime()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));

		when(env.getProperty(eq(SELECTED_AUTH), anyString())).thenReturn("db");
		when(userRepository.countExistingAdmins()).thenReturn(OK.equals(mockResponse) ? 1L : 0L);
		
		// act
		SystemStatusDto response = service.getSystemStatus();
		
		// assert
		Assertions.assertEquals(mockResponse, response.getStatus());
		verify(userRepository).countExistingAdmins();
		assertEquals("db", response.getAuthMode());
		assertEquals("2023-06-29T02:00:00.000+0200", response.getBuilt());
	}
	
	@ParameterizedTest
	@MethodSource("inputData")
	void shouldReturnOkWithDifferentAuthMethod(String selectedAuth, boolean hasBuildProperties, String expectedVersion) {
		when(env.getProperty(eq(SELECTED_AUTH), anyString())).thenReturn(selectedAuth);

		if (hasBuildProperties) {
			when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
			when(clock.getZone()).thenReturn(ZoneId.of("Europe/Budapest"));
			when(buildProperties.getTime()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
			when(buildProperties.getVersion()).thenReturn("1.0.0");
		} else {
			service.setBuildProperties(null);
			when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
			when(clock.getZone()).thenReturn(ZoneId.of("Europe/Budapest"));
		}
		
		if (selectedAuth.equals(SELECTED_AUTH_DB)) {
			when(userRepository.countExistingAdmins()).thenReturn(1L);
		}

		SystemStatusDto response = service.getSystemStatus();
		
		// assert
		Assertions.assertEquals(OK, response.getStatus());
		assertEquals(expectedVersion, response.getVersion());
		verify(userRepository, times(selectedAuth.equals(SELECTED_AUTH_DB) ? 1 : 0)).countExistingAdmins();
		
		if (hasBuildProperties) {
			verify(buildProperties).getTime();
			verify(buildProperties).getVersion();
		}
	}
	
	@Test
	void shouldDeleteCache() {
		service.refreshSystemStatus(new RefreshCacheEvent(this));
		
		assertFalse(logAppender.list.isEmpty());
		assertEquals("System status cache refreshed", logAppender.list.get(0).getFormattedMessage());
	}
	
	public static Object[][] inputData() {
		return new Object[][] {
			{SELECTED_AUTH_LDAP, false, "DevRuntime" },
			{SELECTED_AUTH_DB, true, "1.0.0" },
			{SELECTED_AUTH_DB, true, "1.0.0" }
		};
	}
}
