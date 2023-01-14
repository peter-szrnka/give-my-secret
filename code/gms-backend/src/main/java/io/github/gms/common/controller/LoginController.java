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
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.util.Constants;
import io.github.gms.common.util.CookieUtils;
import io.github.gms.secure.dto.UserInfoDto;
import io.github.gms.secure.service.LoginService;
import io.github.gms.secure.service.SystemPropertyService;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
public class LoginController {
	
	public static final String LOGIN_PATH = "/authenticate";
	public static final String LOGOUT_PATH = "/logoutUser";
	
	@Value("${config.cookie.secure}")
	private boolean secure;

	@Autowired
	private LoginService service;
	
	@Autowired
	private SystemPropertyService systemPropertyService;

	@PostMapping(LOGIN_PATH)
	public ResponseEntity<UserInfoDto> loginAuthentication(@RequestBody AuthenticateRequestDto dto, HttpServletRequest request) {
		AuthenticateResponseDto authenticateResult = service.login(dto);
		
		if (authenticateResult.getToken() == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
		
		HttpHeaders headers = new HttpHeaders();
		
		CookieUtils.addCookie(headers, Constants.ACCESS_JWT_TOKEN, authenticateResult.getToken(), 
				systemPropertyService.getLong(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS), secure);
		CookieUtils.addCookie(headers, Constants.REFRESH_JWT_TOKEN, authenticateResult.getRefreshToken(), 
				systemPropertyService.getLong(SystemProperty.REFRESH_JWT_EXPIRATION_TIME_SECONDS), secure);

		return ResponseEntity.ok().headers(headers).body(authenticateResult.getCurrentUser());
	}
	
	@PostMapping(LOGOUT_PATH)
	public ResponseEntity<Void> logout(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		CookieUtils.addCookie(headers, Constants.ACCESS_JWT_TOKEN, "", 0, secure);
		CookieUtils.addCookie(headers, Constants.REFRESH_JWT_TOKEN, "", 0, secure);
		return ResponseEntity.ok().headers(headers).build();
	}
	
	/*@PostMapping("/refresh")
	public ResponseEntity<Void> refresh(HttpServletRequest request) {
		Cookie jwtTokenCookie = WebUtils.getCookie(request, Constants.REFRESH_JWT_TOKEN);

		HttpHeaders headers = new HttpHeaders();
		addCookie(headers, Constants.ACCESS_JWT_TOKEN, service.refreshToken(jwtTokenCookie.getValue()), 
				systemPropertyService.getLong(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS));

		return ResponseEntity.ok().headers(headers).build();
	}*/
}