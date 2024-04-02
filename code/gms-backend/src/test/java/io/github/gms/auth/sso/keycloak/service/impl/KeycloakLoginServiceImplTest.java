package io.github.gms.auth.sso.keycloak.service.impl;

import io.github.gms.auth.sso.keycloak.Input;
import io.github.gms.auth.sso.keycloak.config.KeycloakSettings;
import io.github.gms.auth.sso.keycloak.model.LoginResponse;
import io.github.gms.auth.sso.keycloak.service.OAuthService;
import io.github.gms.util.TestUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.util.MultiValueMap;

import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static io.github.gms.common.util.Constants.AUDIENCE;
import static io.github.gms.common.util.Constants.CLIENT_ID;
import static io.github.gms.common.util.Constants.CLIENT_SECRET;
import static io.github.gms.common.util.Constants.CREDENTIAL;
import static io.github.gms.common.util.Constants.GRANT_TYPE;
import static io.github.gms.common.util.Constants.REFRESH_JWT_TOKEN;
import static io.github.gms.common.util.Constants.SCOPE;
import static io.github.gms.common.util.Constants.SCOPE_GMS;
import static io.github.gms.common.util.Constants.USERNAME;
import static io.github.gms.util.DemoData.CREDENTIAL_TEST;
import static io.github.gms.util.DemoData.USERNAME1;
import static io.github.gms.util.TestUtils.MOCK_ACCESS_TOKEN;
import static io.github.gms.util.TestUtils.MOCK_REFRESH_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class KeycloakLoginServiceImplTest {

    private OAuthService oAuthService;
    private HttpServletRequest httpServletRequest;
    private KeycloakSettings keycloakSettings;
    private KeycloakLoginServiceImpl service;

    @BeforeEach
    public void setup() {
        oAuthService = mock(OAuthService.class);
        httpServletRequest = mock(HttpServletRequest.class);
        keycloakSettings = mock(KeycloakSettings.class);

        service = new KeycloakLoginServiceImpl(oAuthService, httpServletRequest, keycloakSettings);
    }

    @Test
    void shouldLogin() {
        // arrange
        when(keycloakSettings.getRealm()).thenReturn("gms");
        when(keycloakSettings.getKeycloakTokenUrl()).thenReturn(TestUtils.LOCALHOST_8080);
        when(keycloakSettings.getClientId()).thenReturn("clientId");
        when(keycloakSettings.getClientSecret()).thenReturn("clientSecret");
        when(oAuthService.callEndpoint(eq(TestUtils.LOCALHOST_8080), any(MultiValueMap.class), eq(LoginResponse.class)))
                .thenReturn(LoginResponse.builder().accessToken(MOCK_ACCESS_TOKEN).refreshToken(MOCK_REFRESH_TOKEN).build());

        // act
        LoginResponse response = service.login(USERNAME1, CREDENTIAL_TEST);

        // assert
        assertNotNull(response);
        assertEquals(MOCK_ACCESS_TOKEN, response.getAccessToken());
        assertEquals(MOCK_REFRESH_TOKEN, response.getRefreshToken());
        ArgumentCaptor<MultiValueMap<String, String>> argumentCaptor = ArgumentCaptor.forClass(MultiValueMap.class);
        verify(oAuthService).callEndpoint(eq(TestUtils.LOCALHOST_8080), argumentCaptor.capture(), eq(LoginResponse.class));
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
    void shouldLogout() {
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
        verify(oAuthService).callEndpoint(eq(TestUtils.LOCALHOST_8080), argumentCaptor.capture(), eq(Void.class));
        MultiValueMap<String, String> captured = argumentCaptor.getValue();
        assertEquals("clientId", captured.get(CLIENT_ID).getFirst());
        assertEquals("clientSecret", captured.get(CLIENT_SECRET).getFirst());
    }

    @ParameterizedTest
    @MethodSource("emptyInputData")
    void shouldReturnEmptyInfo(Input input) {
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
        verify(oAuthService, never()).callEndpoint(eq(TestUtils.LOCALHOST_8080), any(MultiValueMap.class), eq(Void.class));
    }

    private static Object[] emptyInputData() {
        return new Object[]{
                new Input(new Cookie[]{}),
                new Input(new Cookie[]{ new Cookie(ACCESS_JWT_TOKEN, "access") }),
                new Input(new Cookie[]{ new Cookie(REFRESH_JWT_TOKEN, "refresh") })
        };
    }
}
