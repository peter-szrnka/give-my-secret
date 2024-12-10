package io.github.gms.functions.api;

import io.github.gms.functions.secret.dto.GetSecretRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiControllerTest {

    private final ApiService service = mock(ApiService.class);

    @Test
    void getSecret_whenInputIsValid_thenReturnData() {
        // arrange
        Map<String, String> mockResponse = Map.of("value", "x");
        when(service.getSecret(any(GetSecretRequestDto.class))).thenReturn(mockResponse);
        ApiController controller = new ApiController(service);

        // act
        Map<String, String> response = controller.getSecret("api-key", "secret-id-1");

        // arrange
        assertNotNull(response);
        assertEquals("x", response.get("value"));
    }
}
