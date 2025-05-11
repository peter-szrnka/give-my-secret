package io.github.gms.common.util;

import org.springframework.http.ResponseCookie;

import java.time.Duration;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class CookieUtils {
	
	private CookieUtils() {}

	public static ResponseCookie createCookie(String name, String value, long maxAge, boolean secure) {
		return createCookie(name, value, maxAge, secure, true);
	}

	public static ResponseCookie createCookie(String name, String value, long maxAge, boolean secure, boolean httpOnly) {
		return ResponseCookie.from(name, value)
				.maxAge(Duration.ofSeconds(maxAge))
				.secure(secure)
				.httpOnly(httpOnly)
				.sameSite(secure ? "None" : "Lax")
				.path("/")
				.build();
	}
}