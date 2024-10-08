package io.github.gms.common.controller;

import io.github.gms.common.dto.ErrorCodeDto;
import io.github.gms.common.dto.ErrorCodeListDto;
import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.common.enums.ContainerHostType;
import io.github.gms.common.types.ErrorCode;
import io.github.gms.functions.system.SystemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 */
@ExtendWith(MockitoExtension.class)
class SystemControllerTest {

    private final SystemService systemService = mock(SystemService.class);
    private SystemController controller;

    @BeforeEach
    void setup() {
        controller = new SystemController(systemService);
    }

    @Test
    void shouldReturnSecret() {
        SystemStatusDto mockResponseDto = SystemStatusDto.builder()
                .withAuthMode("db")
                .withStatus("OK")
                .withVersion("test")
                .withBuilt("local")
                .withContainerHostType(ContainerHostType.DOCKER)
                .withContainerId("containerId")
                .build();

        // arrange
        when(systemService.getSystemStatus()).thenReturn(mockResponseDto);

        // act
        SystemStatusDto response = controller.status();

        // arrange
        assertNotNull(response);
        assertEquals(mockResponseDto, response);
    }

    @Test
    void shouldReturnErrorCodes() {
        // act
        ErrorCodeListDto response = controller.getErrorCodes();
        List<String> codes = response.getErrorCodeList().stream().map(ErrorCodeDto::getCode).toList();

        // arrange
        assertNotNull(response);
        response.getErrorCodeList().forEach(errorCodeDto -> {
            assertNotNull(errorCodeDto.getCode());
            assertNotNull(errorCodeDto.getDescription());
        });
        assertEquals(ErrorCode.values().length, Stream.of(ErrorCode.values()).filter(item -> codes.contains(item.getCode())).count());
    }
}
