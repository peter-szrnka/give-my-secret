
package io.github.gms.secure.service.impl;

import static io.github.gms.common.util.Constants.OK;
import static io.github.gms.common.util.Constants.SELECTED_AUTH;
import static io.github.gms.common.util.Constants.SELECTED_AUTH_DB;
import static io.github.gms.common.util.Constants.SELECTED_AUTH_LDAP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.common.event.RefreshCacheEvent;
import io.github.gms.secure.repository.UserRepository;

/**
 * Unit test of {@link SystemServiceImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class SystemServiceImplTest extends AbstractLoggingUnitTest {

	@InjectMocks
	private SystemServiceImpl service;
	
	@Mock
	private Environment env;

	@Mock
	private UserRepository userRepository;

	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		((Logger) LoggerFactory.getLogger(SystemServiceImpl.class)).addAppender(logAppender);
	}
	
	@ParameterizedTest
	@ValueSource(strings = { "NEED_SETUP", OK })
	void shouldReturnSystemStatus(String mockResponse) {
		when(env.getProperty(eq(SELECTED_AUTH), anyString())).thenReturn("db");
		when(userRepository.countExistingAdmins()).thenReturn(OK.equals(mockResponse) ? 1L : 0L);
		
		SystemStatusDto response = service.getSystemStatus();
		
		// assert
		Assertions.assertEquals(mockResponse, response.getStatus());
		verify(userRepository).countExistingAdmins();
	}
	
	@ParameterizedTest
	@ValueSource(strings = { SELECTED_AUTH_LDAP, SELECTED_AUTH_DB })
	void shouldReturnOkWithDifferentAuthMethod(String selectedAuth) {
		when(env.getProperty(eq(SELECTED_AUTH), anyString())).thenReturn(selectedAuth);
		
		if (selectedAuth.equals(SELECTED_AUTH_DB)) {
			when(userRepository.countExistingAdmins()).thenReturn(1L);
		}

		SystemStatusDto response = service.getSystemStatus();
		
		// assert
		Assertions.assertEquals(OK, response.getStatus());
		verify(userRepository, times(selectedAuth.equals(SELECTED_AUTH_DB) ? 1 : 0)).countExistingAdmins();
	}
	
	@Test
	void shouldDeleteCache() {
		service.refreshSystemStatus(new RefreshCacheEvent(this));
		
		assertFalse(logAppender.list.isEmpty());
		assertEquals("System status cache refreshed", logAppender.list.get(0).getFormattedMessage());
	}
}
