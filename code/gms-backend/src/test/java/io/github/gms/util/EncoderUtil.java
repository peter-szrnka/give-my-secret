package io.github.gms.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author Peter Szrnka
 */
public class EncoderUtil {

	private static final BCryptPasswordEncoder BCRYPT_ENCODER = new BCryptPasswordEncoder();

	private static final String YOUR_CREDENTIAL = "password";

	public static void main(String[] args) {
		System.out.println("BCrypt encoded password: " + BCRYPT_ENCODER.encode(YOUR_CREDENTIAL));
	}
}
