package io.github.gms.functions.api;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.functions.secret.GetSecretRequestDto;
import io.github.gms.functions.secret.SecretEntity;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static io.github.gms.common.util.Constants.VALUE;
import static io.github.gms.util.TestUtils.assertLogContains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test of {@link ApiService}
 *
 * @author Peter Szrnka
 * @since 1.0
 */
class ApiServiceTest extends AbstractUnitTest {

    private static final GetSecretRequestDto dto = new GetSecretRequestDto("12345678", "123456");
    private ListAppender<ILoggingEvent> logAppender;
    private SecretPreparationService secretPreparationService;
    private SecretValueProviderService secretValueProviderService;
    private ApiService service;

    @BeforeEach
    void beforeEach() {
        secretPreparationService = mock(SecretPreparationService.class);
        secretValueProviderService = mock(SecretValueProviderService.class);
        service = new ApiService(secretPreparationService, secretValueProviderService);

        logAppender = new ListAppender<>();
        logAppender.start();
        ((Logger) LoggerFactory.getLogger(ApiService.class)).addAppender(logAppender);
    }

    @AfterEach
    void tearDown() {
        logAppender.list.clear();
        logAppender.stop();
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
