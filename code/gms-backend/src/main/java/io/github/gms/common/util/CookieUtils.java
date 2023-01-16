package io.github.gms.common.util;

import java.time.Duration;

import org.springframework.http.ResponseCookie;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class CookieUtils {

	public static ResponseCookie createCookie(String name, String value, long maxAge, boolean secure) {
		return ResponseCookie.from(name, value)
				.maxAge(Duration.ofSeconds(maxAge))
				.secure(secure)
				.httpOnly(true)
				.sameSite(secure ? "None" : "Lax")
				.path("/")
				.build();
	}
}