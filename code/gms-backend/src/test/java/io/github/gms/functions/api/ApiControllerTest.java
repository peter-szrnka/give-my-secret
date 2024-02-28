package io.github.gms.functions.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.gms.functions.secret.GetSecretRequestDto;

@ExtendWith(MockitoExtension.class)
class ApiControllerTest {
    
    private ApiController controller;
    private ApiService service = mock(ApiService.class);

    @Test
    void shouldReturnSecret() {
        // arrange
        Map<String, String> mockResponse = Map.of("value", "x");
        when(service.getSecret(any(GetSecretRequestDto.class))).thenReturn(mockResponse);
        controller = new ApiController(service);

        // act
        Map<String, String> response = controller.getSecret("api-key", "secret-id-1");

        // arrange
        assertNotNull(response);
        assertEquals("x", response.get("value"));
    }
}
