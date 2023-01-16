package io.github.gms.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

import io.github.gms.abstraction.AbstractUnitTest;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class CookieUtilsTest extends AbstractUnitTest {

	@Test
	void shouldReturnCookie() {
		// act
		ResponseCookie cookie = CookieUtils.createCookie("the-cookie", "new-value", 60l, false);
		
		// assert
		assertNotNull(cookie);
		assertEquals("the-cookie", cookie.getName());
		assertEquals("new-value", cookie.getValue());
		assertEquals(Duration.ofSeconds(60l), cookie.getMaxAge());
		assertFalse(cookie.isSecure());
	}
}