package io.github.gms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;
import java.time.Clock;

import org.jboss.logging.MDC;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.method.HandlerMethod;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.dto.ErrorResponseDto;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.exception.GmsException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Disabled
class GmsExceptionHandlerTest extends AbstractUnitTest {

	private static final String CORRELATION_ID = "CORRELATION_ID";

	private GmsExceptionHandler handler;

	@BeforeEach
	public void setup() {
		MDC.put(MdcParameter.CORRELATION_ID.getDisplayName(), CORRELATION_ID);
		Clock clock = Clock.systemDefaultZone();
		handler = new GmsExceptionHandler(clock);
	}

	@Test
	void shouldHandleGmsException() {
		// arrange
		HttpServletRequest request = mock(HttpServletRequest.class);
		HandlerMethod handlerMethod = mock(HandlerMethod.class);

		// act
		ErrorResponseDto response = handler.handleOtherException(request, handlerMethod, new GmsException("Oops!"));

		// assert
		assertNotNull(response);
		assertEquals(CORRELATION_ID, response.getCorrelationId());
		assertEquals("Oops!", response.getMessage());
	}

	@Test
	void shouldHandleAccessDeniedException() {
		// arrange
		HttpServletRequest request = mock(HttpServletRequest.class);
		HandlerMethod handlerMethod = mock(HandlerMethod.class);

		// act
		ErrorResponseDto response = handler.handleOtherException(request, handlerMethod,
				new AccessDeniedException("Oops!"));

		// assert
		assertNotNull(response);
		assertEquals(CORRELATION_ID, response.getCorrelationId());
		assertEquals("Oops!", response.getMessage());
	}

	@Test
	void shouldHandleOtherException() {
		// arrange
		HttpServletRequest request = mock(HttpServletRequest.class);
		HandlerMethod handlerMethod = mock(HandlerMethod.class);

		// act
		ErrorResponseDto response = handler.handleOtherException(request, handlerMethod, new RuntimeException("Oops!"));

		// assert
		assertNotNull(response);
		assertEquals(CORRELATION_ID, response.getCorrelationId());
		assertEquals("Oops!", response.getMessage());
	}

	@Test
	void shouldHandleMissingRequestHeaderException() {
		// arrange
		HttpServletRequest request = mock(HttpServletRequest.class);
		HandlerMethod handlerMethod = mock(HandlerMethod.class);
		Method mockMethod = mock(Method.class);
		MethodParameter mockMethodParameter = new MethodParameter(mockMethod, -1, 2);

		// act
		ErrorResponseDto response = handler.handleOtherException(request, handlerMethod,
				new MissingRequestHeaderException("x-api-key", mockMethodParameter));

		// assert
		assertNotNull(response);
		assertEquals(CORRELATION_ID, response.getCorrelationId());
		assertEquals("Required request header 'x-api-key' for method parameter type Object is not present", response.getMessage());
	}
}
