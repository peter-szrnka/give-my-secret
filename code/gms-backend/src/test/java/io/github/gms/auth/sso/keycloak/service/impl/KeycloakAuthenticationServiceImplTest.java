package io.github.gms.auth.sso.keycloak.service.impl;

import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.auth.sso.keycloak.converter.KeycloakConverter;
import io.github.gms.auth.sso.keycloak.model.IntrospectResponse;
import io.github.gms.auth.sso.keycloak.service.KeycloakIntrospectService;
import io.github.gms.auth.sso.keycloak.service.KeycloakLoginService;
import io.github.gms.auth.types.AuthResponsePhase;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.common.util.Constants;
import io.github.gms.functions.user.UserEntity;
import io.github.gms.functions.user.UserRepository;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;

import static io.github.gms.util.TestUtils.MOCK_ACCESS_TOKEN;
import static io.github.gms.util.TestUtils.MOCK_REFRESH_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
    private KeycloakAuthenticationServiceImpl service;

    @BeforeEach
    public void setup() {
        keycloakLoginService = mock(KeycloakLoginService.class);
        keycloakIntrospectService = mock(KeycloakIntrospectService.class);
        converter = mock(KeycloakConverter.class);
        userRepository = mock(UserRepository.class);
        service = new KeycloakAuthenticationServiceImpl(keycloakLoginService, keycloakIntrospectService, converter, userRepository);
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
    void shouldLoginSucceed() {
        // arrange
        when(keycloakLoginService.login(DemoData.USERNAME1, DemoData.CREDENTIAL_TEST))
                .thenReturn(Map.of(
                        Constants.ACCESS_TOKEN, MOCK_ACCESS_TOKEN,
                        Constants.REFRESH_TOKEN, MOCK_REFRESH_TOKEN
                ));
        UserInfoDto mockUserInfo = TestUtils.createUserInfoDto();
        when(converter.toUserInfoDto(any(IntrospectResponse.class))).thenReturn(mockUserInfo);
        IntrospectResponse mockIntrospectResponse = IntrospectResponse.builder()
                .email("email@email")
                .name("name")
                .active("true")
                .build();
        when(keycloakIntrospectService.getUserDetails(MOCK_ACCESS_TOKEN, MOCK_REFRESH_TOKEN))
                .thenReturn(mockIntrospectResponse);
        UserEntity userEntity = TestUtils.createUser();
        when(converter.toNewEntity(any(UserEntity.class), eq(mockUserInfo))).thenReturn(userEntity);
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
    }

    @Test
    void shouldLogout() {
        // act
        service.logout();

        // assert
        verify(keycloakLoginService).logout();
    }
}
