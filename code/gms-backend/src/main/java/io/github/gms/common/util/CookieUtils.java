package io.github.gms.common.util;

import javax.servlet.http.Cookie;

import org.springframework.http.HttpHeaders;

public class CookieUtils {
	
	private static final String COOKIE_FORMAT = "%s=%s;Max-Age=%d;HttpOnly;SameSite=%s%s";
	
	private CookieUtils() {}
	
	public static void addCookie(HttpHeaders headers, String name, String value, long maxAge, boolean secure) {
		headers.add(Constants.SET_COOKIE, getValue(name, value, maxAge, secure));
	}

	public static Cookie addCookie(String name, String value, long maxAge, boolean secure) {
		return new Cookie(Constants.SET_COOKIE, getValue(name, value, maxAge, secure));
	}
	
	private static String getValue(String name, String value, long maxAge, boolean secure) {
		return String.format(COOKIE_FORMAT, name, value, maxAge, secure ? "None" : "Lax", secure ? ";Secure" : "");
	}
}