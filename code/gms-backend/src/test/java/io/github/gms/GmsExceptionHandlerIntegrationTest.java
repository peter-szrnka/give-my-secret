package io.github.gms;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.common.types.ErrorCode;
import io.github.gms.common.types.GmsException;
import io.github.gms.functions.system.SystemService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import static io.github.gms.util.TestConstants.TAG_INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@Tag(TAG_INTEGRATION_TEST)
class GmsExceptionHandlerIntegrationTest extends AbstractIntegrationTest {

    @MockBean
    private SystemService systemService;

    @Test
    void shouldHandleGmsException() {
        // arrange
        when(systemService.getSystemStatus()).thenThrow(new GmsException("Test exception", ErrorCode.GMS_026));
        // act
        HttpEntity<Void> requestEntity = new HttpEntity<>(null);
        ResponseEntity<SystemStatusDto> response = executeHttpGet("/system/status", requestEntity, SystemStatusDto.class);

        // assert
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void shouldHandleAccessDeniedException() {
        // arrange
        when(systemService.getSystemStatus()).thenThrow(new AccessDeniedException("Access denied"));
        // act
        HttpEntity<Void> requestEntity = new HttpEntity<>(null);
        ResponseEntity<SystemStatusDto> response = executeHttpGet("/system/status", requestEntity, SystemStatusDto.class);

        // assert
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
