package io.github.gms.common.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import io.github.gms.auth.dto.AuthenticateRequestDto;
import io.github.gms.auth.dto.AuthenticateResponseDto;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.util.Constants;
import io.github.gms.common.util.CookieUtils;
import io.github.gms.secure.dto.UserInfoDto;
import io.github.gms.secure.service.LoginService;
import io.github.gms.secure.service.SystemPropertyService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Unit test of {@link LoginController}
 * 
 * @author Peter Szrnka
 */
@ExtendWith(MockitoExtension.class)
class LoginControllerTest {
    
    private LoginController controller;
    private LoginService service;
	private SystemPropertyService systemPropertyService;

    @BeforeEach
    void setup() {
        service = mock(LoginService.class);
        systemPropertyService = mock(SystemPropertyService.class);
        controller = new LoginController(service, systemPropertyService, false);
    }

    @Test
    void shouldLoginFail() {
        // arrange
        AuthenticateRequestDto dto = new AuthenticateRequestDto("user", "pass");
        HttpServletRequest request = mock(HttpServletRequest.class);

        AuthenticateResponseDto mockResponse = new AuthenticateResponseDto();
        when(service.login(dto)).thenReturn(mockResponse);

        // act
        ResponseEntity<UserInfoDto> response = controller.loginAuthentication(dto, request);

        // assert
        assertNotNull(response);
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void shouldLogin() {
        // arrange
        AuthenticateRequestDto dto = new AuthenticateRequestDto("user", "pass");
        HttpServletRequest request = mock(HttpServletRequest.class);

        AuthenticateResponseDto mockResponse = new AuthenticateResponseDto();
        mockResponse.setRefreshToken("REFRESHTOKEN");
        mockResponse.setToken("TOKEN");
        when(service.login(dto)).thenReturn(mockResponse);

        when(systemPropertyService.getLong(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS)).thenReturn(2L);
        when(systemPropertyService.getLong(SystemProperty.REFRESH_JWT_EXPIRATION_TIME_SECONDS)).thenReturn(3L);

        MockedStatic<CookieUtils> mockCookieUtils = mockStatic(CookieUtils.class);

        ResponseCookie accessJwtCookie = mock(ResponseCookie.class);
        when(accessJwtCookie.toString()).thenReturn("mock-cookie1");
        mockCookieUtils.when(() -> CookieUtils.createCookie(eq(Constants.ACCESS_JWT_TOKEN), eq("TOKEN"), eq(2L), eq(false))).thenReturn(accessJwtCookie);

        ResponseCookie refreshJwtCookie = mock(ResponseCookie.class);
        when(refreshJwtCookie.toString()).thenReturn("mock-cookie2");
        mockCookieUtils.when(() -> CookieUtils.createCookie(eq(Constants.REFRESH_JWT_TOKEN), eq("REFRESHTOKEN"), eq(3L), eq(false))).thenReturn(refreshJwtCookie);

        // act
        ResponseEntity<UserInfoDto> response = controller.loginAuthentication(dto, request);

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getHeaders().size());
        assertTrue(response.getHeaders().get("Set-Cookie").stream().anyMatch(item -> item.equals("mock-cookie1")));
        assertTrue(response.getHeaders().get("Set-Cookie").stream().anyMatch(item -> item.equals("mock-cookie2")));

        verify(systemPropertyService).getLong(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
		verify(systemPropertyService).getLong(SystemProperty.REFRESH_JWT_EXPIRATION_TIME_SECONDS);

        mockCookieUtils.close();
    }

    @Test
    void shouldLogout() {
        // arrange
        HttpServletRequest request = mock(HttpServletRequest.class);

        // act
        ResponseEntity<Void> response = controller.logout(request);
    
        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getHeaders().size());
        assertTrue(response.getHeaders().get("Set-Cookie").stream().anyMatch(item -> item.equals("jwt=; Path=/; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT; HttpOnly; SameSite=Lax")));
        assertTrue(response.getHeaders().get("Set-Cookie").stream().anyMatch(item -> item.equals("refreshJwt=; Path=/; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT; HttpOnly; SameSite=Lax")));
    }
}