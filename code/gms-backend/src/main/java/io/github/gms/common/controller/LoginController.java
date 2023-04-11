package io.github.gms.common.controller;

import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static io.github.gms.common.util.Constants.REFRESH_JWT_TOKEN;
import static io.github.gms.common.util.Constants.SET_COOKIE;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.gms.auth.dto.AuthenticateRequestDto;
import io.github.gms.auth.dto.AuthenticateResponseDto;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.util.CookieUtils;
import io.github.gms.secure.dto.UserInfoDto;
import io.github.gms.secure.service.LoginService;
import io.github.gms.secure.service.SystemPropertyService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/")
public class LoginController {
	
	public static final String LOGIN_PATH = "authenticate";
	public static final String LOGOUT_PATH = "logoutUser";

	private final LoginService service;
	private final SystemPropertyService systemPropertyService;
	private final boolean secure;

	public LoginController(LoginService service, SystemPropertyService systemPropertyService,
						   @Value("${config.cookie.secure}") boolean secure) {
		this.service = service;
		this.systemPropertyService = systemPropertyService;
		this.secure = secure;
	}

	@PostMapping(LOGIN_PATH)
	public ResponseEntity<UserInfoDto> loginAuthentication(@RequestBody AuthenticateRequestDto dto, HttpServletRequest request) {
		AuthenticateResponseDto authenticateResult = service.login(dto);
		
		if (authenticateResult.getToken() == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
		
		HttpHeaders headers = new HttpHeaders();
		
		headers.add(SET_COOKIE, CookieUtils.createCookie(ACCESS_JWT_TOKEN, authenticateResult.getToken(),
				systemPropertyService.getLong(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS), secure).toString());
		headers.add(SET_COOKIE, CookieUtils.createCookie(REFRESH_JWT_TOKEN, authenticateResult.getRefreshToken(),
				systemPropertyService.getLong(SystemProperty.REFRESH_JWT_EXPIRATION_TIME_SECONDS), secure).toString());

		return ResponseEntity.ok().headers(headers).body(authenticateResult.getCurrentUser());
	}
	
	@PostMapping(LOGOUT_PATH)
	public ResponseEntity<Void> logout(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		
		headers.add(SET_COOKIE, CookieUtils.createCookie(ACCESS_JWT_TOKEN, null, 0, secure).toString());
		headers.add(SET_COOKIE, CookieUtils.createCookie(REFRESH_JWT_TOKEN, null, 0, secure).toString());

		return ResponseEntity.ok().headers(headers).build();
	}
}