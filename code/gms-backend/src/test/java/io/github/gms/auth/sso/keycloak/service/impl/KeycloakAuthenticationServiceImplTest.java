package io.github.gms.auth.sso.keycloak.service.impl;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
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
import static io.github.gms.util.LogAssertionUtils.assertLogContains;
import static io.github.gms.util.TestUtils.MOCK_ACCESS_TOKEN;
import static io.github.gms.util.TestUtils.MOCK_REFRESH_TOKEN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class KeycloakAuthenticationServiceImplTest extends AbstractLoggingUnitTest {

    private KeycloakLoginService keycloakLoginService;
    private KeycloakIntrospectService keycloakIntrospectService;
    private KeycloakConverter converter;
    private UserRepository userRepository;
    private HttpServletRequest httpServletRequest;
    private KeycloakAuthenticationServiceImpl service;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        keycloakLoginService = mock(KeycloakLoginService.class);
        keycloakIntrospectService = mock(KeycloakIntrospectService.class);
        converter = mock(KeycloakConverter.class);
        userRepository = mock(UserRepository.class);
        httpServletRequest = mock(HttpServletRequest.class);
        service = new KeycloakAuthenticationServiceImpl(keycloakLoginService, keycloakIntrospectService, converter, userRepository, httpServletRequest);
        addAppender(KeycloakAuthenticationServiceImpl.class);
    }

    @Test
    void shouldLoginSkippedWhenIntrospectFailed() {
        // arrange
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[] {
                new Cookie(ACCESS_JWT_TOKEN, MOCK_ACCESS_TOKEN),
                new Cookie(REFRESH_JWT_TOKEN, MOCK_REFRESH_TOKEN)
        });
        when(keycloakIntrospectService.getUserDetails(MOCK_ACCESS_TOKEN, MOCK_REFRESH_TOKEN))
                .thenReturn(ResponseEntity.status(400).body(IntrospectResponse.builder().errorDescription("Account disabled").build()));

        // act
        AuthenticationResponse response = service.authenticate(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST);

        // assert
        assertNotNull(response);
        assertEquals(AuthResponsePhase.ALREADY_LOGGED_IN, response.getPhase());
        verify(keycloakLoginService, never()).login(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST);
        verify(converter, never()).toUserInfoDto(any(IntrospectResponse.class));
        verify(keycloakIntrospectService).getUserDetails(MOCK_ACCESS_TOKEN, MOCK_REFRESH_TOKEN);
        verify(httpServletRequest, times(2)).getCookies();
    }

    @Test
    void shouldLoginSkipped() {
        // arrange
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[] {
                new Cookie(ACCESS_JWT_TOKEN, MOCK_ACCESS_TOKEN),
                new Cookie(REFRESH_JWT_TOKEN, MOCK_REFRESH_TOKEN)
        });
        UserInfoDto mockUserInfo = TestUtils.createUserInfoDto();
        mockUserInfo.setFailedAttempts(2);
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
        assertNull(response.getCurrentUser().getStatus());
        assertNull(response.getCurrentUser().getFailedAttempts());
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

    @ParameterizedTest
    @MethodSource("not2xxInputData")
    void shouldLoginFailWhenResponseIsNot2xx(String errorDescription, AuthResponsePhase authResponsePhaseExpected) {
        // arrange
        when(keycloakLoginService.login(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST))
                .thenReturn(ResponseEntity.status(400).body(LoginResponse.builder()
                        .error("error").errorDescription(errorDescription).build()));

        // act
        AuthenticationResponse response = service.authenticate(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST);

        // assert
        assertNotNull(response);
        assertEquals(authResponsePhaseExpected, response.getPhase());
        verify(keycloakLoginService).login(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST);
        verify(converter, never()).toUserInfoDto(any(IntrospectResponse.class));
        verify(keycloakIntrospectService, never()).getUserDetails(MOCK_ACCESS_TOKEN, MOCK_REFRESH_TOKEN);
        assertLogContains(logAppender, "Login failed! Status code=400");
    }

    private static Object[][] not2xxInputData() {
        return new Object[][] {
                { "Account disabled", AuthResponsePhase.BLOCKED },
                { "Other issue", AuthResponsePhase.FAILED }
        };
    }

    @Test
    void shouldLoginFailedWhenIntrospectFailed() {
        // arrange
        when(keycloakLoginService.login(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST))
                .thenReturn(ResponseEntity.ok(LoginResponse.builder()
                        .accessToken(MOCK_ACCESS_TOKEN)
                        .refreshToken(MOCK_REFRESH_TOKEN)
                        .build()));
        when(keycloakIntrospectService.getUserDetails(MOCK_ACCESS_TOKEN, MOCK_REFRESH_TOKEN))
                .thenReturn(ResponseEntity.badRequest().build());

        // act
        AuthenticationResponse response = service.authenticate(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST);

        // assert
        assertNotNull(response);
        assertEquals(AuthResponsePhase.FAILED, response.getPhase());
        verify(keycloakLoginService).login(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST);
        verify(keycloakIntrospectService).getUserDetails(MOCK_ACCESS_TOKEN, MOCK_REFRESH_TOKEN);
    }

    @Test
    void shouldLoginFailedWhenResponseBodyMissing() {
        // arrange
        when(keycloakLoginService.login(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST))
                .thenReturn(ResponseEntity.ok().build());

        // act
        AuthenticationResponse response = service.authenticate(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST);

        // assert
        assertNotNull(response);
        assertNull(response.getCurrentUser());
        assertNull(response.getToken());
        assertNull(response.getRefreshToken());
        assertEquals(AuthResponsePhase.FAILED, response.getPhase());
        verify(keycloakLoginService).login(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST);
    }

    @Test
    void shouldLoginFailedWhenIntrospectReturnsInactive() {
        // arrange
        when(keycloakLoginService.login(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST))
                .thenReturn(ResponseEntity.ok(LoginResponse.builder()
                        .accessToken(MOCK_ACCESS_TOKEN)
                        .refreshToken(MOCK_REFRESH_TOKEN)
                        .build()));
        IntrospectResponse mockIntrospectResponse = IntrospectResponse.builder().active("false")
                .build();
        when(keycloakIntrospectService.getUserDetails(MOCK_ACCESS_TOKEN, MOCK_REFRESH_TOKEN))
                .thenReturn(ResponseEntity.ok(mockIntrospectResponse));

        // act
        AuthenticationResponse response = service.authenticate(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST);

        // assert
        assertNotNull(response);
        assertEquals(AuthResponsePhase.FAILED, response.getPhase());
        verify(keycloakLoginService).login(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST);
        verify(converter, never()).toUserInfoDto(any(IntrospectResponse.class));
        verify(keycloakIntrospectService).getUserDetails(MOCK_ACCESS_TOKEN, MOCK_REFRESH_TOKEN);
    }

    @Test
    void shouldLoginFailedWhenIntrospectReturnsEmptyBody() {
        // arrange
        when(keycloakLoginService.login(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST))
                .thenReturn(ResponseEntity.ok(LoginResponse.builder()
                        .accessToken(MOCK_ACCESS_TOKEN)
                        .refreshToken(MOCK_REFRESH_TOKEN)
                        .build()));
        when(keycloakIntrospectService.getUserDetails(MOCK_ACCESS_TOKEN, MOCK_REFRESH_TOKEN))
                .thenReturn(ResponseEntity.ok().build());

        // act
        AuthenticationResponse response = service.authenticate(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST);

        // assert
        assertNotNull(response);
        assertNull(response.getCurrentUser());
        assertNull(response.getToken());
        assertNull(response.getRefreshToken());
        assertEquals(AuthResponsePhase.FAILED, response.getPhase());
        verify(keycloakLoginService).login(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST);
        verify(converter, never()).toUserInfoDto(any(IntrospectResponse.class));
        verify(keycloakIntrospectService).getUserDetails(MOCK_ACCESS_TOKEN, MOCK_REFRESH_TOKEN);
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
        when(converter.toEntity(any(UserEntity.class), eq(mockUserInfo))).thenReturn(userEntity);
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
        verify(converter).toEntity(any(UserEntity.class), eq(mockUserInfo));
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
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(userEntity));
        when(converter.toEntity(any(UserEntity.class), eq(mockUserInfo))).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);

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
        verify(converter).toEntity(any(UserEntity.class), eq(mockUserInfo));
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
