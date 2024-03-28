package io.github.gms.auth.sso.keycloak.service.impl;

import io.github.gms.abstraction.AbstractUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
public class KeycloakOAuthServiceImplTest extends AbstractUnitTest {

    public static final String URL = "http://localhost";
    private RestTemplate restTemplate;
    private KeycloakOAuthServiceImpl service;

    @BeforeEach
    public void setup() {
        restTemplate = mock(RestTemplate.class);
        service = new KeycloakOAuthServiceImpl(restTemplate);
    }

    @Test
    void test() {
        // arrange
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add(CLIENT_ID, "client-id");
        requestBody.add(CLIENT_SECRET, "client-secret");
        requestBody.add(TOKEN, "accessToken");
        requestBody.add(REFRESH_TOKEN, "refreshToken");
        ResponseEntity<String> mockResponseEntity = ResponseEntity.ok("ok");
        when(restTemplate.postForEntity(eq(URL), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponseEntity);

        // act
        String response = service.callEndpoint(URL, requestBody, String.class);

        // assert
        assertNotNull(response);
        assertEquals("ok", response);
        verify(restTemplate).postForEntity(eq(URL), any(HttpEntity.class), eq(String.class));
    }
}
