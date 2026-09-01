package io.github.gms.auth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.gms.ZonedDateTimeTypeAdapter;
import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.dto.ErrorResponseDto;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.util.ThreadLocalContext;
import io.github.gms.util.TestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.util.MimeTypeUtils;
import tools.jackson.databind.json.JsonMapper;

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

	private JsonMapper jsonMapper;
	private GmsAuthenticationEntryPoint entryPoint;
	
	@BeforeEach
	void setup() {
		Clock clock = mock(Clock.class);
		jsonMapper = mock(JsonMapper.class);
		entryPoint = new GmsAuthenticationEntryPoint(jsonMapper, clock);
		setupClock(clock);
	}
	
	@Test
	@SneakyThrows
	void commence_whenInvalidCookieExceptionOccurred_thenReturnCustomResponse() {
		// given
		ThreadLocalContext.set(MdcParameter.CORRELATION_ID, "CORRELATION_ID");
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
		AuthenticationException exception = new InvalidCookieException("Invalid cookie");
		PrintWriter mockWriter = mock(PrintWriter.class);
		when(httpServletResponse.getWriter()).thenAnswer(invocation -> {
			assertEquals("CORRELATION_ID", ThreadLocalContext.getAsString(MdcParameter.CORRELATION_ID));
			return mockWriter;
		});

		String json = gson.toJson(TestUtils.createErrorResponseDto(exception));
		when(jsonMapper.writeValueAsString(any(ErrorResponseDto.class))).thenReturn(json);

		// when
		entryPoint.commence(httpServletRequest, httpServletResponse, exception);
		
		// then
		assertNull(ThreadLocalContext.getAsString(MdcParameter.CORRELATION_ID));
		verify(mockWriter).write(anyString());
		ArgumentCaptor<ErrorResponseDto> errorResponseDtoCaptor = ArgumentCaptor.forClass(ErrorResponseDto.class);
		verify(jsonMapper).writeValueAsString(errorResponseDtoCaptor.capture());

		assertEquals("GmsAuthenticationEntryPoint: Invalid cookie", errorResponseDtoCaptor.getValue().getMessage());
		assertEquals("CORRELATION_ID", errorResponseDtoCaptor.getValue().getCorrelationId());
		assertEquals("GMS-000", errorResponseDtoCaptor.getValue().getErrorCode());

		verify(httpServletResponse).setStatus(HttpStatus.FORBIDDEN.value());
		verify(httpServletResponse).setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
		assertNull(ThreadLocalContext.getAsString(MdcParameter.CORRELATION_ID));

		ThreadLocalContext.remove(MdcParameter.CORRELATION_ID);
	}
}

