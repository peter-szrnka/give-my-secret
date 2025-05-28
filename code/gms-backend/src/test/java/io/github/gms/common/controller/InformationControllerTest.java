package io.github.gms.common.controller;

import io.github.gms.common.dto.*;
import io.github.gms.common.enums.ContainerHostType;
import io.github.gms.common.types.ErrorCode;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.user.UserInfoService;
import io.github.gms.util.TestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class InformationControllerTest {

    private UserInfoService userInfoService;
    private SystemService systemService;
    private InformationController controller;
    
    @BeforeEach()
    void setup() {
        userInfoService = mock(UserInfoService.class);
        systemService = mock(SystemService.class);
        controller = new InformationController(userInfoService, systemService);
    }


    @Test
    void getVmOptions_whenDataSaved_thenReturnOptions() {
        // arrange
        when(systemService.getVmOptions()).thenReturn(List.of(new VmOptionDto("key", "value")));

        // act
        List<VmOptionDto> response = controller.getVmOptions();

        // assert
        assertNotNull(response);
        assertEquals(1, response.size());
        verify(systemService).getVmOptions();
    }

    @Test
    void getUserInfo_whenUserInfoAvailable_thenReturnDto() {
        // arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(userInfoService.getUserInfo(request)).thenReturn(TestUtils.createUserInfoDto());

        // act
        UserInfoDto response = controller.getUserInfo(request);

        // assert
        assertNotNull(response);
        verify(userInfoService).getUserInfo(request);
    }

    @Test
    void status_whenCalled_thenReturnSystemStatus() {
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
    void getErrorCodes_whenCalled_thenReturnAllErrorCodes() {
        // act
        ErrorCodeListDto response = controller.getErrorCodes();
        List<String> codes = response.getErrorCodeList().stream().map(ErrorCodeDto::getCode).toList();

        // arrange
        assertNotNull(response);
        response.getErrorCodeList().forEach(errorCodeDto -> assertNotNull(errorCodeDto.getCode()));
        assertEquals(ErrorCode.values().length, Stream.of(ErrorCode.values()).filter(item -> codes.contains(item.getCode())).count());
    }
}
