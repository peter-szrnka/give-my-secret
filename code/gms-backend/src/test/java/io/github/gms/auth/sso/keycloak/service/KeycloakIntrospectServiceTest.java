package io.github.gms.auth.sso.keycloak.service;

import io.github.gms.auth.sso.keycloak.config.KeycloakSettings;
import io.github.gms.auth.sso.keycloak.model.IntrospectResponse;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import static io.github.gms.common.util.Constants.CLIENT_ID;
import static io.github.gms.common.util.Constants.CLIENT_SECRET;
import static io.github.gms.common.util.Constants.REFRESH_TOKEN;
import static io.github.gms.common.util.Constants.TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class KeycloakIntrospectServiceTest {

    private KeycloakOAuthService oAuthService;
    private KeycloakSettings keycloakSettings;
    private KeycloakIntrospectService service;

    @BeforeEach
    public void setup() {
        oAuthService = mock(KeycloakOAuthService.class);
        keycloakSettings = mock(KeycloakSettings.class);
        service = new KeycloakIntrospectService(oAuthService, keycloakSettings);
    }

    @Test
    void getUserDetails_whenInputProvided_thenReturnOK() {
        // arrange
        when(keycloakSettings.getClientId()).thenReturn("clientId");
        when(keycloakSettings.getClientSecret()).thenReturn("clientSecret");
        when(keycloakSettings.getIntrospectUrl()).thenReturn(TestUtils.LOCALHOST_8080);

        IntrospectResponse mockIntrospectResponse = IntrospectResponse.builder()
                .email("email@email")
                .name("name")
                .active("true")
                .build();
        when(oAuthService.callPostEndpoint(eq(TestUtils.LOCALHOST_8080), any(MultiValueMap.class), eq(IntrospectResponse.class)))
                .thenReturn(ResponseEntity.ok(mockIntrospectResponse));

        // act
        ResponseEntity<IntrospectResponse> response = service.getUserDetails("accessToken", "refreshToken");

        // assert
        assertNotNull(response);
        verify(keycloakSettings).getClientId();
        verify(keycloakSettings).getClientSecret();
        verify(keycloakSettings).getIntrospectUrl();
        ArgumentCaptor<MultiValueMap<String, String>> argumentCaptor = ArgumentCaptor.forClass(MultiValueMap.class);
        verify(oAuthService).callPostEndpoint(eq(TestUtils.LOCALHOST_8080), argumentCaptor.capture(), eq(IntrospectResponse.class));

        MultiValueMap<String, String> captured = argumentCaptor.getValue();
        assertEquals("clientId", captured.get(CLIENT_ID).getFirst());
        assertEquals("clientSecret", captured.get(CLIENT_SECRET).getFirst());
        assertEquals("accessToken", captured.get(TOKEN).getFirst());
        assertEquals("refreshToken", captured.get(REFRESH_TOKEN).getFirst());
    }
}
