package io.github.gms.auth.sso.keycloak.service.impl;

import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.auth.sso.keycloak.converter.KeycloakConverter;
import io.github.gms.auth.sso.keycloak.model.IntrospectResponse;
import io.github.gms.auth.sso.keycloak.model.LoginResponse;
import io.github.gms.auth.sso.keycloak.service.KeycloakIntrospectService;
import io.github.gms.auth.sso.keycloak.service.KeycloakLoginService;
import io.github.gms.auth.types.AuthResponsePhase;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.functions.user.UserEntity;
import io.github.gms.functions.user.UserRepository;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static io.github.gms.common.util.Constants.REFRESH_JWT_TOKEN;
import static io.github.gms.util.TestUtils.MOCK_ACCESS_TOKEN;
import static io.github.gms.util.TestUtils.MOCK_REFRESH_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
class KeycloakAuthenticationServiceImplTest {

    private KeycloakLoginService keycloakLoginService;
    private KeycloakIntrospectService keycloakIntrospectService;
    private KeycloakConverter converter;
    private UserRepository userRepository;
    private HttpServletRequest httpServletRequest;
    private KeycloakAuthenticationServiceImpl service;

    @BeforeEach
    public void setup() {
        keycloakLoginService = mock(KeycloakLoginService.class);
        keycloakIntrospectService = mock(KeycloakIntrospectService.class);
        converter = mock(KeycloakConverter.class);
        userRepository = mock(UserRepository.class);
        httpServletRequest = mock(HttpServletRequest.class);
        service = new KeycloakAuthenticationServiceImpl(keycloakLoginService, keycloakIntrospectService, converter, userRepository, httpServletRequest);
    }

    @Test
    void shouldLoginSkipped() {
        // arrange
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[] {
                new Cookie(ACCESS_JWT_TOKEN, MOCK_ACCESS_TOKEN),
                new Cookie(REFRESH_JWT_TOKEN, MOCK_REFRESH_TOKEN)
        });
        UserInfoDto mockUserInfo = TestUtils.createUserInfoDto();
        when(converter.toUserInfoDto(any(IntrospectResponse.class))).thenReturn(mockUserInfo);
        IntrospectResponse mockIntrospectResponse = IntrospectResponse.builder()
                .email("email@email")
                .name("name")
                .active("true")
                .build();
        when(keycloakIntrospectService.getUserDetails(MOCK_ACCESS_TOKEN, MOCK_REFRESH_TOKEN))
                .thenReturn(ResponseEntity.ok(mockIntrospectResponse));

        // act
        AuthenticationResponse response = service.authenticate(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST);

        // assert
        assertNotNull(response);
        assertEquals(AuthResponsePhase.ALREADY_LOGGED_IN, response.getPhase());
        verify(keycloakLoginService, never()).login(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST);
        verify(converter).toUserInfoDto(any(IntrospectResponse.class));
        verify(keycloakIntrospectService).getUserDetails(MOCK_ACCESS_TOKEN, MOCK_REFRESH_TOKEN);
        verify(httpServletRequest, times(2)).getCookies();
    }

    @Test
    void shouldLoginFail() {
        // arrange
        when(keycloakLoginService.login(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST))
                .thenThrow(new RuntimeException("Oops!"));

        // act
        AuthenticationResponse response = service.authenticate(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST);

        // assert
        assertNotNull(response);
        assertEquals(AuthResponsePhase.FAILED, response.getPhase());
        verify(keycloakLoginService).login(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST);
        verify(converter, never()).toUserInfoDto(any(IntrospectResponse.class));
    }

    @Test
    void shouldLoginSucceedWhenUserNotFound() {
        // arrange
        when(keycloakLoginService.login(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST))
                .thenReturn(ResponseEntity.ok(LoginResponse.builder()
                        .accessToken(MOCK_ACCESS_TOKEN)
                        .refreshToken(MOCK_REFRESH_TOKEN)
                        .build()));
        UserInfoDto mockUserInfo = TestUtils.createUserInfoDto();
        when(converter.toUserInfoDto(any(IntrospectResponse.class))).thenReturn(mockUserInfo);
        IntrospectResponse mockIntrospectResponse = IntrospectResponse.builder()
                .email("email@email")
                .name("name")
                .active("true")
                .build();
        when(keycloakIntrospectService.getUserDetails(MOCK_ACCESS_TOKEN, MOCK_REFRESH_TOKEN))
                .thenReturn(ResponseEntity.ok(mockIntrospectResponse));
        UserEntity userEntity = TestUtils.createUser();
        when(converter.toNewEntity(any(UserEntity.class), eq(mockUserInfo))).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // act
        AuthenticationResponse response = service.authenticate(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST);

        // assert
        assertNotNull(response);
        assertEquals(AuthResponsePhase.COMPLETED, response.getPhase());
        assertEquals(MOCK_ACCESS_TOKEN, response.getToken());
        assertEquals(MOCK_REFRESH_TOKEN, response.getRefreshToken());
        verify(keycloakLoginService).login(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST);
        ArgumentCaptor<IntrospectResponse> introspectResponseArgumentCaptor = ArgumentCaptor.forClass(IntrospectResponse.class);
        verify(converter).toUserInfoDto(introspectResponseArgumentCaptor.capture());
        assertEquals(mockIntrospectResponse, introspectResponseArgumentCaptor.getValue());
        verify(keycloakIntrospectService).getUserDetails(MOCK_ACCESS_TOKEN, MOCK_REFRESH_TOKEN);
        verify(userRepository).save(userEntity);
        verify(converter).toNewEntity(any(UserEntity.class), eq(mockUserInfo));
    }

    @ParameterizedTest
    @MethodSource("tokenInputData")
    void shouldLoginSucceedWhenUserFound(String accessToken, String refreshToken) {
        List<Cookie> cookies = new ArrayList<>();
        if (accessToken != null) {
            cookies.add(new Cookie(ACCESS_JWT_TOKEN, accessToken));
        }
        if (refreshToken != null) {
            cookies.add(new Cookie(REFRESH_JWT_TOKEN, refreshToken));
        }
        // arrange
        when(httpServletRequest.getCookies()).thenReturn(cookies.toArray(new Cookie[]{}));
        when(keycloakLoginService.login(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST))
                .thenReturn(ResponseEntity.ok(LoginResponse.builder()
                        .accessToken(MOCK_ACCESS_TOKEN)
                        .refreshToken(MOCK_REFRESH_TOKEN)
                        .build()));
        UserInfoDto mockUserInfo = TestUtils.createUserInfoDto();
        when(converter.toUserInfoDto(any(IntrospectResponse.class))).thenReturn(mockUserInfo);
        IntrospectResponse mockIntrospectResponse = IntrospectResponse.builder()
                .email("email@email")
                .name("name")
                .active("true")
                .build();
        when(keycloakIntrospectService.getUserDetails(MOCK_ACCESS_TOKEN, MOCK_REFRESH_TOKEN))
                .thenReturn(ResponseEntity.ok(mockIntrospectResponse));
        UserEntity userEntity = TestUtils.createUser();
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(userEntity));

        // act
        AuthenticationResponse response = service.authenticate(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST);

        // assert
        assertNotNull(response);
        assertEquals(AuthResponsePhase.COMPLETED, response.getPhase());
        assertEquals(MOCK_ACCESS_TOKEN, response.getToken());
        assertEquals(MOCK_REFRESH_TOKEN, response.getRefreshToken());
        verify(keycloakLoginService).login(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST);
        ArgumentCaptor<IntrospectResponse> introspectResponseArgumentCaptor = ArgumentCaptor.forClass(IntrospectResponse.class);
        verify(converter).toUserInfoDto(introspectResponseArgumentCaptor.capture());
        assertEquals(mockIntrospectResponse, introspectResponseArgumentCaptor.getValue());
        verify(keycloakIntrospectService).getUserDetails(MOCK_ACCESS_TOKEN, MOCK_REFRESH_TOKEN);
        verify(userRepository, never()).save(userEntity);
        verify(converter, never()).toNewEntity(any(UserEntity.class), eq(mockUserInfo));
    }

    @Test
    void shouldLogout() {
        // act
        service.logout();

        // assert
        verify(keycloakLoginService).logout();
    }

    private static Object[][] tokenInputData () {
        return new Object[][] {
                { null, null },
                { null, MOCK_REFRESH_TOKEN },
                { MOCK_ACCESS_TOKEN, null }
        };
    }
}
