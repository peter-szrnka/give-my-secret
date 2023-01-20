package io.github.gms.common.filter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.HeaderType;
import io.github.gms.common.exception.GmsException;
import lombok.SneakyThrows;

/**
 * Unit test of {@link ApiHeaderInitializerFilter}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class ApiHeaderInitializerFilterTest extends AbstractUnitTest {

	private static final String API_KEY_VALUE = "API_KEY_VALUE";
	private ApiHeaderInitializerFilter filter = new ApiHeaderInitializerFilter();
	
	@BeforeEach
	public void setup() {
		MDC.clear();
	}
	
	@Test
	@SneakyThrows
	void shouldMissApiKey() {
		// act
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain filterChain = mock(FilterChain.class);

		GmsException exception = assertThrows(GmsException.class, () -> 
			filter.doFilterInternal(request, response, filterChain));
		
		// assert
		assertEquals("API key is missing!", exception.getMessage());
	}

	@Test
	@SneakyThrows
	void shouldNotMissApiKey() {
		// act
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader(HeaderType.API_KEY.getHeaderName())).thenReturn(API_KEY_VALUE);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain filterChain = mock(FilterChain.class);

		// act & assert
		assertDoesNotThrow(() -> filter.doFilterInternal(request, response, filterChain));
	}
}
