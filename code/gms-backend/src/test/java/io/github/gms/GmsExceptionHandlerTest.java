package io.github.gms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.MDC;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.method.HandlerMethod;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.dto.ErrorResponseDto;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.exception.GmsException;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class GmsExceptionHandlerTest extends AbstractUnitTest {
	
	private static final String CORRELATION_ID = "CORRELATION_ID";

	@InjectMocks
	private GmsExceptionHandler handler;
	
	@BeforeEach
	public void setup() {
		MDC.put(MdcParameter.CORRELATION_ID.getDisplayName(), CORRELATION_ID);
		setupClock();
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
		ErrorResponseDto response = handler.handleOtherException(request, handlerMethod, new AccessDeniedException("Oops!"));
		
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
}
