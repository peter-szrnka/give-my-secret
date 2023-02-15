package io.github.gms.auth;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.time.Clock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.MDC;
import org.springframework.security.core.AuthenticationException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.dto.ErrorResponseDto;
import io.github.gms.common.enums.MdcParameter;
import lombok.SneakyThrows;

/**
 * Unit test of {@link GmsAuthenticationEntryPoint}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class GmsAuthenticationEntryPointTest extends AbstractUnitTest {

	@Mock
	private Clock clock;
	@Mock
	private ObjectMapper objectMapper;
	@InjectMocks
	private GmsAuthenticationEntryPoint entryPoint;
	
	@BeforeEach
	void setup() {
		setupClock(clock);
	}
	
	@Test
	@SneakyThrows
	void test() {
		// arrange
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
		AuthenticationException exception = mock(AuthenticationException.class);
		PrintWriter mockWriter = mock(PrintWriter.class);
		when(httpServletResponse.getWriter()).thenReturn(mockWriter);
		when(objectMapper.writeValueAsString(any(ErrorResponseDto.class))).thenReturn("{}");

		// act
		entryPoint.commence(httpServletRequest, httpServletResponse, exception);
		
		// assert
		assertNull(MDC.get(MdcParameter.CORRELATION_ID.getDisplayName()));
		verify(mockWriter).write(anyString());
		verify(objectMapper).writeValueAsString(any(ErrorResponseDto.class));
	}
}
