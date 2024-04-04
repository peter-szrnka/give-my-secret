package io.github.gms.common.controller;

import io.github.gms.auth.AuthenticationService;
import io.github.gms.auth.dto.AuthenticateRequestDto;
import io.github.gms.auth.dto.AuthenticateResponseDto;
import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.auth.types.AuthResponsePhase;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.util.Constants;
import io.github.gms.common.util.CookieUtils;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static io.github.gms.common.util.Constants.REFRESH_JWT_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test of {@link LoginController}
 * 
 * @author Peter Szrnka
 */
@ExtendWith(MockitoExtension.class)
class LoginControllerTest {
    
    private LoginController controller;
    private AuthenticationService service;
	private SystemPropertyService systemPropertyService;

    @BeforeEach
    void setup() {
        service = mock(AuthenticationService.class);
        systemPropertyService = mock(SystemPropertyService.class);
        controller = new LoginController(service, systemPropertyService, false);
    }

    @Test
    void shouldLoginFail() {
        try (MockedStatic<CookieUtils> cookieUtilsMockedStatic = mockStatic(CookieUtils.class)) {
            // arrange
            AuthenticateRequestDto dto = new AuthenticateRequestDto("user", "pass");
            AuthenticationResponse mockResponse = new AuthenticationResponse();
            mockResponse.setPhase(AuthResponsePhase.FAILED);
            when(service.authenticate("user", "pass")).thenReturn(mockResponse);
            cookieUtilsMockedStatic.when(() -> CookieUtils.createCookie(eq(ACCESS_JWT_TOKEN), isNull(), eq(0L), eq(false)))
                    .thenReturn(TestUtils.createResponseCookie(ACCESS_JWT_TOKEN));
            cookieUtilsMockedStatic.when(() -> CookieUtils.createCookie(eq(REFRESH_JWT_TOKEN), isNull(), eq(0L), eq(false)))
                    .thenReturn(TestUtils.createResponseCookie(REFRESH_JWT_TOKEN));

            // act
            ResponseEntity<AuthenticateResponseDto> response = controller.loginAuthentication(dto);

            // assert
            assertNotNull(response);
            assertEquals(401, response.getStatusCode().value());
        }
    }

    @Test
    void shouldLogin() {
        // arrange
        AuthenticateRequestDto dto = new AuthenticateRequestDto("user", "pass");

        AuthenticationResponse mockResponse = new AuthenticationResponse();
        mockResponse.setRefreshToken("REFRESHTOKEN");
        mockResponse.setToken("TOKEN");
        mockResponse.setCurrentUser(TestUtils.createUserInfoDto());
        mockResponse.setPhase(AuthResponsePhase.COMPLETED);
        when(service.authenticate("user", "pass")).thenReturn(mockResponse);

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
        ResponseEntity<AuthenticateResponseDto> response = controller.loginAuthentication(dto);

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getHeaders().size());
        assertTrue(Objects.requireNonNull(response.getHeaders().get("Set-Cookie")).stream().anyMatch(item -> item.equals("mock-cookie1")));
        assertTrue(Objects.requireNonNull(response.getHeaders().get("Set-Cookie")).stream().anyMatch(item -> item.equals("mock-cookie2")));
        assertEquals("AuthenticateResponseDto(currentUser=UserInfoDto(id=1, name=name, username=user, email=a@b.com, role=ROLE_USER, status=ACTIVE, failedAttempts=null), phase=COMPLETED)",
                Objects.requireNonNull(response.getBody()).toString());

        verify(systemPropertyService).getLong(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
		verify(systemPropertyService).getLong(SystemProperty.REFRESH_JWT_EXPIRATION_TIME_SECONDS);

        mockCookieUtils.verify(() -> CookieUtils.createCookie(eq(Constants.ACCESS_JWT_TOKEN), eq("TOKEN"), eq(2L), eq(false)));
        mockCookieUtils.verify(() -> CookieUtils.createCookie(eq(Constants.REFRESH_JWT_TOKEN), eq("REFRESHTOKEN"), eq(3L), eq(false)));
        mockCookieUtils.close();
    }

    @Test
    void shouldLoginRequireMfa() {
        // arrange
        AuthenticateRequestDto dto = new AuthenticateRequestDto("user", "pass");

        AuthenticationResponse mockResponse = new AuthenticationResponse();
        mockResponse.setPhase(AuthResponsePhase.MFA_REQUIRED);
        when(service.authenticate("user", "pass")).thenReturn(mockResponse);

        // act
        ResponseEntity<AuthenticateResponseDto> response = controller.loginAuthentication(dto);

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(0, response.getHeaders().size());
        assertEquals(AuthResponsePhase.MFA_REQUIRED, Objects.requireNonNull(response.getBody()).getPhase());
        assertEquals("AuthenticateResponseDto(currentUser=null, phase=MFA_REQUIRED)", response.getBody().toString());

        verify(systemPropertyService, never()).getLong(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
		verify(systemPropertyService, never()).getLong(SystemProperty.REFRESH_JWT_EXPIRATION_TIME_SECONDS);
    }

    @Test
    void shouldLogout() {
        // act
        ResponseEntity<Void> response = controller.logout();
    
        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getHeaders().size());
        assertTrue(Objects.requireNonNull(response.getHeaders().get("Set-Cookie"))
                .stream().anyMatch(item -> item.equals("jwt=; Path=/; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT; HttpOnly; SameSite=Lax")));
        assertTrue(Objects.requireNonNull(response.getHeaders().get("Set-Cookie"))
                .stream().anyMatch(item -> item.equals("refreshJwt=; Path=/; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT; HttpOnly; SameSite=Lax")));
    }
}