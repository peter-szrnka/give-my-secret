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

import io.github.gms.auth.AuthenticationService;
import io.github.gms.auth.dto.AuthenticateRequestDto;
import io.github.gms.auth.dto.AuthenticateResponseDto;
import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.auth.types.AuthResponsePhase;
import io.github.gms.common.dto.LoginVerificationRequestDto;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.util.CookieUtils;
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

	private final AuthenticationService service;
	private final SystemPropertyService systemPropertyService;
	private final boolean secure;

	public LoginController(AuthenticationService service, SystemPropertyService systemPropertyService,
						   @Value("${config.cookie.secure}") boolean secure) {
		this.service = service;
		this.systemPropertyService = systemPropertyService;
		this.secure = secure;
	}

	@PostMapping(LOGIN_PATH)
	public ResponseEntity<AuthenticateResponseDto> loginAuthentication(@RequestBody AuthenticateRequestDto dto, HttpServletRequest request) {
		AuthenticationResponse authenticateResult = service.authenticate(dto.getUsername(), dto.getCredential());

		if (AuthResponsePhase.FAILED == authenticateResult.getPhase()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}

		return ResponseEntity.ok().headers(addHeaders(authenticateResult))
			.body(AuthenticateResponseDto.builder()
				.currentUser(authenticateResult.getCurrentUser())
				.phase(authenticateResult.getPhase())
				.build());
	}

	@PostMapping("verify")
	public ResponseEntity<AuthenticateResponseDto> verify(@RequestBody LoginVerificationRequestDto dto) {
		AuthenticationResponse authenticateResult = service.verify(dto);

		if (AuthResponsePhase.FAILED == authenticateResult.getPhase()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}

		return ResponseEntity.ok().headers(addHeaders(authenticateResult)).body(AuthenticateResponseDto.builder()
			.currentUser(authenticateResult.getCurrentUser())
			.phase(authenticateResult.getPhase())
			.build());
	}
	
	@PostMapping(LOGOUT_PATH)
	public ResponseEntity<Void> logout(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		
		headers.add(SET_COOKIE, CookieUtils.createCookie(ACCESS_JWT_TOKEN, null, 0, secure).toString());
		headers.add(SET_COOKIE, CookieUtils.createCookie(REFRESH_JWT_TOKEN, null, 0, secure).toString());

		return ResponseEntity.ok().headers(headers).build();
	}

	private HttpHeaders addHeaders(AuthenticationResponse authenticateResult) {
		HttpHeaders headers = new HttpHeaders();

		addHeader(headers, ACCESS_JWT_TOKEN, authenticateResult.getToken(), SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
		addHeader(headers, REFRESH_JWT_TOKEN, authenticateResult.getRefreshToken(), SystemProperty.REFRESH_JWT_EXPIRATION_TIME_SECONDS);

		return headers;
	}

	private void addHeader(HttpHeaders headers, String tokenName, String tokenValue, SystemProperty property) {
		if (tokenValue == null) {
			return;
		}

		headers.add(SET_COOKIE, CookieUtils.createCookie(tokenName, tokenValue,
				systemPropertyService.getLong(property), secure).toString());
	}
}