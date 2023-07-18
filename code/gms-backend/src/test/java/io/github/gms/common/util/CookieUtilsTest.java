package io.github.gms.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseCookie;

import io.github.gms.abstraction.AbstractUnitTest;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class CookieUtilsTest extends AbstractUnitTest {

    @Test
    void shouldTestPrivateConstructor() {
        assertPrivateConstructor(CookieUtils.class);
    }

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void shouldReturnCookie(boolean secure) {
		// act
		ResponseCookie cookie = CookieUtils.createCookie("the-cookie", "new-value", 60l, secure);
		
		// assert
		assertNotNull(cookie);
		assertEquals("the-cookie", cookie.getName());
		assertEquals("new-value", cookie.getValue());
		assertEquals(Duration.ofSeconds(60l), cookie.getMaxAge());
		assertEquals(secure, cookie.isSecure());
		assertEquals(secure ? "None" : "Lax", cookie.getSameSite());
	}
}