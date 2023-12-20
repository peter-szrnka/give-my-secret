package io.github.gms;

import io.github.gms.common.dto.ErrorResponseDto;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.exception.GmsException;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.context.request.WebRequest;

import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class GmsExceptionHandlerTest {

	private static final String CORRELATION_ID = "CORRELATION_ID";

	private GmsExceptionHandler handler;

	@BeforeEach
	void setup() {
		MDC.put(MdcParameter.CORRELATION_ID.getDisplayName(), CORRELATION_ID);
		Clock clock = mock(Clock.class);
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		handler = new GmsExceptionHandler(clock);
	}

	@Test
	void shouldHandleGmsException() {
		// arrange
		WebRequest webRequest = mock(WebRequest.class);

		// act
		ResponseEntity<ErrorResponseDto> response = handler.handleGmsException(new GmsException("Oops!"), webRequest);

		// assert
		assertNotNull(response);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(CORRELATION_ID, response.getBody().getCorrelationId());
		assertEquals("Oops!", response.getBody().getMessage());
	}

	@Test
	void shouldHandleAccessDeniedException() {
		// arrange
		WebRequest webRequest = mock(WebRequest.class);

		// act
		ResponseEntity<ErrorResponseDto> response = handler.handleAccessDeniedException(new AccessDeniedException("Oops!"), webRequest);

		// assert
		assertNotNull(response);
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(CORRELATION_ID, response.getBody().getCorrelationId());
		assertEquals("Oops!", response.getBody().getMessage());
	}

	@Test
	void shouldHandleOtherException() {
		// arrange
		WebRequest webRequest = mock(WebRequest.class);

		// act
		ResponseEntity<ErrorResponseDto> response = handler.handleOtherException(new RuntimeException("Oops!"), webRequest);

		// assert
		assertNotNull(response);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(CORRELATION_ID, response.getBody().getCorrelationId());
		assertEquals("Oops!", response.getBody().getMessage());
	}

	@Test
	void shouldHandleMissingRequestHeaderException() {
		// arrange
		Method mockMethod = mock(Method.class);
		MethodParameter mockMethodParameter = new MethodParameter(mockMethod, -1, 2);
		WebRequest webRequest = mock(WebRequest.class);

		// act
		ResponseEntity<ErrorResponseDto> response = handler.handleMissingRequestHeaderException(new MissingRequestHeaderException("x-api-key", mockMethodParameter), webRequest);

		// assert
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(CORRELATION_ID, response.getBody().getCorrelationId());
		assertEquals("Required request header 'x-api-key' for method parameter type Object is not present", response.getBody().getMessage());
	}
}