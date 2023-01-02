package io.github.gms.common.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.gms.auth.dto.AuthenticateRequestDto;
import io.github.gms.auth.dto.AuthenticateResponseDto;
import io.github.gms.common.util.Constants;
import io.github.gms.secure.dto.UserInfoDto;
import io.github.gms.secure.service.LoginService;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
public class OpenController {
	
	public static final String LOGIN_PATH = "/authenticate";
	public static final String LOGOUT_PATH = "/logoutUser";
	private static final String COOKIE_FORMAT = "%s=%s;Max-Age=%d;HttpOnly;SameSite=%s%s";
	
	@Value("${config.cookie.secure}")
	private boolean secure;

	@Autowired
	private LoginService service;

	@PostMapping(LOGIN_PATH)
	public ResponseEntity<UserInfoDto> loginAuthentication(@RequestBody AuthenticateRequestDto dto, HttpServletRequest request) {
		AuthenticateResponseDto authenticateResult = service.login(dto);
		
		if (authenticateResult.getToken() == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
		
		HttpHeaders headers = new HttpHeaders();
		
		addCookie(headers, authenticateResult.getToken(), Constants.VALIDITY_SECONDS);

		return ResponseEntity.ok().headers(headers).body(authenticateResult.getCurrentUser());
	}
	
	@PostMapping(LOGOUT_PATH)
	public ResponseEntity<Void> logout(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		addCookie(headers, "", 0);
		return ResponseEntity.ok().headers(headers).build();
	}
	
	private void addCookie(HttpHeaders headers, String value, long maxAge) {
	    headers.add(Constants.SET_COOKIE, String.format(COOKIE_FORMAT, Constants.JWT_TOKEN, value, maxAge, secure ? "None" : "Lax", secure ? ";Secure" : ""));
	}
}