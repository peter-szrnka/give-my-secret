package io.github.gms.auth.sso.keycloak.service.impl;

import io.github.gms.auth.sso.keycloak.Input;
import io.github.gms.auth.sso.keycloak.model.IntrospectResponse;
import io.github.gms.auth.sso.keycloak.model.RealmAccess;
import io.github.gms.auth.sso.keycloak.service.KeycloakIntrospectService;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.common.enums.UserRole;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static io.github.gms.common.util.Constants.REFRESH_JWT_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class KeycloakUserInfoServiceImplTest {

    private KeycloakIntrospectService keycloakIntrospectService;
    private KeycloakUserInfoServiceImpl service;

    @BeforeEach
    public void setup() {
        keycloakIntrospectService = mock(KeycloakIntrospectService.class);

        service = new KeycloakUserInfoServiceImpl(keycloakIntrospectService);
    }

    @ParameterizedTest
    @MethodSource("emptyInputData")
    void shouldReturnEmptyInfo(Input input) {
        // arrange
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getCookies()).thenReturn(input.getCookies());

        // act
        UserInfoDto response = service.getUserInfo(httpServletRequest);

        // assert
        assertNotNull(response);
        assertNull(response.getEmail());
        assertNull(response.getName());
        assertNull(response.getUsername());
        assertTrue(response.getRoles().isEmpty());
        verify(httpServletRequest, times(2)).getCookies();
        verify(keycloakIntrospectService, never()).getUserDetails("access", "refresh");
    }

    @Test
    void shouldReturnUserInfo() {
        // arrange
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[]{
                new Cookie(ACCESS_JWT_TOKEN, "access"),
                new Cookie(REFRESH_JWT_TOKEN, "refresh")
        });
        when(keycloakIntrospectService.getUserDetails("access", "refresh"))
                .thenReturn(IntrospectResponse.builder()
                        .name("My Name")
                        .username("user1")
                        .active("active")
                        .email("email@email")
                        .realmAccess(RealmAccess.builder().roles(List.of("ROLE_USER")).build())
                        .build());

        // act
        UserInfoDto response = service.getUserInfo(httpServletRequest);

        // assert
        assertNotNull(response);
        assertEquals("email@email", response.getEmail());
        assertEquals("My Name", response.getName());
        assertEquals("user1", response.getUsername());
        assertEquals(UserRole.ROLE_USER, response.getRoles().iterator().next());
        verify(httpServletRequest, times(2)).getCookies();
        verify(keycloakIntrospectService).getUserDetails("access", "refresh");
    }

    private static Object[] emptyInputData() {
        return new Object[]{
                new Input(new Cookie[]{}),
                new Input(new Cookie[]{ new Cookie(ACCESS_JWT_TOKEN, "access") }),
                new Input(new Cookie[]{ new Cookie(REFRESH_JWT_TOKEN, "refresh") })
        };
    }
}
