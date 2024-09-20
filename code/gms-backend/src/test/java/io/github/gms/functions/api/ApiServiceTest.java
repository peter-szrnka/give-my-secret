package io.github.gms.functions.api;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.functions.secret.GetSecretRequestDto;
import io.github.gms.functions.secret.SecretEntity;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.github.gms.common.util.Constants.VALUE;
import static io.github.gms.util.LogAssertionUtils.assertLogContains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Unit test of {@link ApiService}
 *
 * @author Peter Szrnka
 * @since 1.0
 */
class ApiServiceTest extends AbstractLoggingUnitTest {

    private static final GetSecretRequestDto dto = new GetSecretRequestDto("12345678", "123456");
    private SecretPreparationService secretPreparationService;
    private SecretValueProviderService secretValueProviderService;
    private ApiService service;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        secretPreparationService = mock(SecretPreparationService.class);
        secretValueProviderService = mock(SecretValueProviderService.class);
        service = new ApiService(secretPreparationService, secretValueProviderService);

        addAppender(ApiService.class);
    }

    @Test
    void shouldReturnSecret() {
        // arrange
        SecretEntity mockEntity = TestUtils.createSecretEntity();
        Map<String, String> mockResponse = Map.of(VALUE, "my-value");
        when(secretPreparationService.getSecretEntity(dto)).thenReturn(mockEntity);
        when(secretValueProviderService.getSecretValue(mockEntity)).thenReturn(mockResponse);

        // act
        Map<String, String> response = service.getSecret(dto);

        // assert
        assertNotNull(response);
        assertEquals("my-value", response.get(VALUE));
		assertLogContains(logAppender, "Searching for secret=");
		verify(secretPreparationService).getSecretEntity(dto);
		verify(secretValueProviderService).getSecretValue(mockEntity);
    }
}
