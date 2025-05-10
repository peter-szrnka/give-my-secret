package io.github.gms.common.util;

import io.github.gms.abstraction.AbstractUnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class CookieUtilsTest extends AbstractUnitTest {

    @Test
    void test_whenConstructorCalled_thenSuccessfullyInstantiated() {
        assertPrivateConstructor(CookieUtils.class);
    }

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void createCookie_whenValidParametersProvided_thenReturnCookie(boolean secure) {
		// when
		ResponseCookie cookie = CookieUtils.createCookie("the-cookie", "new-value", 60L, secure);
		
		// then
		assertNotNull(cookie);
		assertEquals("the-cookie", cookie.getName());
		assertEquals("new-value", cookie.getValue());
		assertEquals(Duration.ofSeconds(60L), cookie.getMaxAge());
		assertEquals(secure, cookie.isSecure());
		assertEquals(secure ? "None" : "Lax", cookie.getSameSite());
		assertTrue(cookie.isHttpOnly());
	}

	@Test
	void createCookie_whenNullValuesProvided_thenReturnCookie() {
		// when
		ResponseCookie cookie = CookieUtils.createCookie("the-cookie", null, 0L, true);

		// then
		assertNotNull(cookie);
	}

	@Test
	void createCookie_whenHttpOnlyFalse_thenReturnCookie() {
		// when
		ResponseCookie cookie = CookieUtils.createCookie("the-cookie", "new-value", 60L, true, false);

		// then
		assertNotNull(cookie);
		assertEquals("the-cookie", cookie.getName());
		assertEquals("new-value", cookie.getValue());
		assertEquals(Duration.ofSeconds(60L), cookie.getMaxAge());
		assertTrue(cookie.isSecure());
		assertFalse(cookie.isHttpOnly());
	}
}