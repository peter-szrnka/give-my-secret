package io.github.gms.auth.sso.keycloak.service;

import io.github.gms.abstraction.AbstractUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import static io.github.gms.common.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class KeycloakOAuthServiceTest extends AbstractUnitTest {

    public static final String URL = "http://localhost";
    private RestClient restClient;
    private KeycloakOAuthService service;

    @BeforeEach
    void setup() {
        restClient = mock(RestClient.class);
        service = new KeycloakOAuthService(restClient);
    }

    @Test
    void callPostEndpoint_whenInputProvided_thenReturnOK() {
        // arrange
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add(CLIENT_ID, "client-id");
        requestBody.add(CLIENT_SECRET, "client-secret");
        requestBody.add(TOKEN, "accessToken");
        requestBody.add(REFRESH_TOKEN, "refreshToken");
        ResponseEntity<String> mockResponseEntity = ResponseEntity.ok("ok");
        when(restClient.post()
                .uri(anyString())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(requestBody)
                .retrieve()
                .toEntity(String.class))
                .thenReturn(mockResponseEntity);

        // act
        ResponseEntity<String> response = service.callPostEndpoint(URL, requestBody, String.class);

        // assert
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals("ok", response.getBody());
        ArgumentCaptor<HttpEntity<?>> argument = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restClient.post()
                .uri(anyString())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(requestBody)
                .retrieve()
                .toEntity(String.class));
    }
}
