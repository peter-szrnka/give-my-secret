package io.github.gms.common.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.functions.system.SystemService;

/**
 * @author Peter Szrnka
 */
@ExtendWith(MockitoExtension.class)
class SystemControllerTest {
    
    private SystemController controller;
    private SystemService systemService = mock(SystemService.class);

    @Test
    void shouldReturnSecret() {
        SystemStatusDto mockResponseDto = new SystemStatusDto("db", "OK", "test", "local");
        // arrange
        when(systemService.getSystemStatus()).thenReturn(mockResponseDto);
        controller = new SystemController(systemService);

        // act
        SystemStatusDto response = controller.status();

        // arrange
        assertNotNull(response);
        assertEquals(mockResponseDto, response);
    }
}
