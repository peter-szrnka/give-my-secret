package io.github.gms.auth.sso.keycloak.service;

import io.github.gms.auth.sso.keycloak.Input;
import io.github.gms.auth.sso.keycloak.config.KeycloakSettings;
import io.github.gms.auth.sso.keycloak.model.LoginResponse;
import io.github.gms.util.TestUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import static io.github.gms.common.util.Constants.*;
import static io.github.gms.util.DemoData.CREDENTIAL_TEST;
import static io.github.gms.util.DemoData.USERNAME1;
import static io.github.gms.util.TestUtils.MOCK_ACCESS_TOKEN;
import static io.github.gms.util.TestUtils.MOCK_REFRESH_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class KeycloakLoginServiceTest {

    private KeycloakOAuthService oAuthService;
    private HttpServletRequest httpServletRequest;
    private KeycloakSettings keycloakSettings;
    private KeycloakLoginService service;

    @BeforeEach
    public void setup() {
        oAuthService = mock(KeycloakOAuthService.class);
        httpServletRequest = mock(HttpServletRequest.class);
        keycloakSettings = mock(KeycloakSettings.class);

        service = new KeycloakLoginService(oAuthService, httpServletRequest, keycloakSettings);
    }

    @Test
    void login_whenCorrectInputProvided_thenLogIn() {
        // arrange
        when(keycloakSettings.getRealm()).thenReturn("gms");
        when(keycloakSettings.getKeycloakTokenUrl()).thenReturn(TestUtils.LOCALHOST_8080);
        when(keycloakSettings.getClientId()).thenReturn("clientId");
        when(keycloakSettings.getClientSecret()).thenReturn("clientSecret");
        when(oAuthService.callPostEndpoint(eq(TestUtils.LOCALHOST_8080), any(MultiValueMap.class), eq(LoginResponse.class)))
                .thenReturn(ResponseEntity.ok(LoginResponse.builder().accessToken(MOCK_ACCESS_TOKEN).refreshToken(MOCK_REFRESH_TOKEN).build()));

        // act
        ResponseEntity<LoginResponse> response = service.login(USERNAME1, CREDENTIAL_TEST);

        // assert
        assertNotNull(response);
        assertNotNull(response.getBody());
        LoginResponse payload = response.getBody();
        assertEquals(MOCK_ACCESS_TOKEN, payload.getAccessToken());
        assertEquals(MOCK_REFRESH_TOKEN, payload.getRefreshToken());
        ArgumentCaptor<MultiValueMap<String, String>> argumentCaptor = ArgumentCaptor.forClass(MultiValueMap.class);
        verify(oAuthService).callPostEndpoint(eq(TestUtils.LOCALHOST_8080), argumentCaptor.capture(), eq(LoginResponse.class));
        verify(keycloakSettings).getRealm();
        verify(keycloakSettings).getKeycloakTokenUrl();
        verify(keycloakSettings).getClientId();
        verify(keycloakSettings).getClientSecret();
        MultiValueMap<String, String> captured = argumentCaptor.getValue();
        assertEquals(CREDENTIAL, captured.get(GRANT_TYPE).getFirst());
        assertEquals("gms", captured.get(AUDIENCE).getFirst());
        assertEquals(USERNAME1, captured.get(USERNAME).getFirst());
        assertEquals(CREDENTIAL_TEST, captured.get(CREDENTIAL).getFirst());
        assertEquals(SCOPE_GMS, captured.get(SCOPE).getFirst());
        assertEquals("clientId", captured.get(CLIENT_ID).getFirst());
        assertEquals("clientSecret", captured.get(CLIENT_SECRET).getFirst());
    }

    @Test
    void logout_whenUserPressedLogout_thenDeleteAllCookies() {
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[]{
                new Cookie(ACCESS_JWT_TOKEN, "access"),
                new Cookie(REFRESH_JWT_TOKEN, "refresh")
        });
        when(keycloakSettings.getClientId()).thenReturn("clientId");
        when(keycloakSettings.getClientSecret()).thenReturn("clientSecret");
        when(keycloakSettings.getLogoutUrl()).thenReturn(TestUtils.LOCALHOST_8080);

        // act
        service.logout();

        // assert
        verify(httpServletRequest, times(2)).getCookies();
        verify(keycloakSettings).getClientId();
        verify(keycloakSettings).getClientSecret();
        verify(keycloakSettings).getLogoutUrl();
        ArgumentCaptor<MultiValueMap<String, String>> argumentCaptor = ArgumentCaptor.forClass(MultiValueMap.class);
        verify(oAuthService).callPostEndpoint(eq(TestUtils.LOCALHOST_8080), argumentCaptor.capture(), eq(Void.class));
        MultiValueMap<String, String> captured = argumentCaptor.getValue();
        assertEquals("clientId", captured.get(CLIENT_ID).getFirst());
        assertEquals("clientSecret", captured.get(CLIENT_SECRET).getFirst());
        assertEquals("access", captured.get(TOKEN).getFirst());
        assertEquals("refresh", captured.get(REFRESH_TOKEN).getFirst());
    }

    @ParameterizedTest
    @MethodSource("emptyInputData")
    void logout_whenUserAlreadyLoggedOut_thenDoNothing(Input input) {
        // arrange
        when(httpServletRequest.getCookies()).thenReturn(input.getCookies());
        when(keycloakSettings.getClientId()).thenReturn("clientId");
        when(keycloakSettings.getClientSecret()).thenReturn("clientSecret");
        when(keycloakSettings.getLogoutUrl()).thenReturn(TestUtils.LOCALHOST_8080);

        // act
        service.logout();

        // assert
        verify(keycloakSettings, never()).getClientId();
        verify(keycloakSettings, never()).getClientSecret();
        verify(keycloakSettings, never()).getLogoutUrl();
        verify(oAuthService, never()).callPostEndpoint(eq(TestUtils.LOCALHOST_8080), any(MultiValueMap.class), eq(Void.class));
    }

    private static Object[] emptyInputData() {
        return new Object[]{
                new Input(new Cookie[]{}),
                new Input(new Cookie[]{ new Cookie(ACCESS_JWT_TOKEN, "access") }),
                new Input(new Cookie[]{ new Cookie(REFRESH_JWT_TOKEN, "refresh") })
        };
    }
}
