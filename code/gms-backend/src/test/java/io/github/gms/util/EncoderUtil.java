package io.github.gms.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author Peter Szrnka
 */
public class EncoderUtil {

	private static final BCryptPasswordEncoder BRCYPT_ENCODER = new BCryptPasswordEncoder();
	
	private static String YOUR_CREDENTIAL = "password";
	
	public static void main(String[] args) {
		System.out.println("BCrypt encoded password: " + BRCYPT_ENCODER.encode(YOUR_CREDENTIAL));
	}
}
