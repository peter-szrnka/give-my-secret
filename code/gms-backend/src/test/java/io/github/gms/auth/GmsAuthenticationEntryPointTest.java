package io.github.gms.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.gms.ZonedDateTimeTypeAdapter;
import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.dto.ErrorResponseDto;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.util.TestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.util.MimeTypeUtils;

import java.io.PrintWriter;
import java.time.Clock;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class GmsAuthenticationEntryPointTest extends AbstractUnitTest {

	private static final Gson gson = new GsonBuilder()
			.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter())
			.create();

	private ObjectMapper objectMapper;
	private GmsAuthenticationEntryPoint entryPoint;
	
	@BeforeEach
	void setup() {
		Clock clock = mock(Clock.class);
		objectMapper = mock(ObjectMapper.class);
		entryPoint = new GmsAuthenticationEntryPoint(objectMapper, clock);
		setupClock(clock);
	}
	
	@Test
	@SneakyThrows
	void test() {
		// arrange
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
		AuthenticationException exception = new InvalidCookieException("Invalid cookie");
		PrintWriter mockWriter = mock(PrintWriter.class);
		when(httpServletResponse.getWriter()).thenReturn(mockWriter);

		String json = gson.toJson(TestUtils.createErrorResponseDto(exception));
		when(objectMapper.writeValueAsString(any(ErrorResponseDto.class))).thenReturn(json);

		// act
		entryPoint.commence(httpServletRequest, httpServletResponse, exception);
		
		// assert
		assertNull(MDC.get(MdcParameter.CORRELATION_ID.getDisplayName()));
		verify(mockWriter).write(anyString());
		ArgumentCaptor<ErrorResponseDto> errorResponseDtoCaptor = ArgumentCaptor.forClass(ErrorResponseDto.class);
		verify(objectMapper).writeValueAsString(errorResponseDtoCaptor.capture());

		assertEquals("GmsAuthenticationEntryPoint: Invalid cookie", errorResponseDtoCaptor.getValue().getMessage());
		assertNull(errorResponseDtoCaptor.getValue().getCorrelationId());
		assertEquals("GMS-000", errorResponseDtoCaptor.getValue().getErrorCode());

		verify(httpServletResponse).setStatus(HttpStatus.FORBIDDEN.value());
		verify(httpServletResponse).setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
	}
}
