package io.github.gms.functions.setup;

import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.dto.SimpleResponseDto;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.SystemStatus;
import io.github.gms.common.enums.UserRole;
import io.github.gms.functions.user.SaveUserRequestDto;
import io.github.gms.functions.user.UserDto;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test of {@link SetupController}
 * 
 * @author Peter Szrnka
 */
@ExtendWith(MockitoExtension.class)
class SetupControllerTest {
    
    private SetupController controller;
    private SetupService setupService;

    @BeforeEach
    void setup() {
        setupService = mock(SetupService.class);
        controller = new SetupController(setupService);
    }

    @Test
    void shouldGetVmOptions() {
        // arrange
        when(setupService.getVmOptions()).thenReturn(Map.of("key", "value"));

        // act
        Map<String, String> response = controller.getVmOptions();

        // assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("value", response.get("key"));
        verify(setupService).getVmOptions();
    }

    @Test
    void shouldStepBack() {
        // arrange
        when(setupService.stepBack()).thenReturn(SystemStatus.NEED_SETUP.name());

        // act
        String response = controller.stepBack();

        // assert
        assertEquals(SystemStatus.NEED_SETUP.name(), response);
        verify(setupService).stepBack();
    }

    @Test
    void shouldGetCurrentSuperAdmin() {
        // arrange
        when(setupService.getCurrentSuperAdmin()).thenReturn(TestUtils.createUserDto());

        // act
        UserDto response = controller.getCurrentSuperAdmin();

        // assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(setupService).getCurrentSuperAdmin();
    }

    @Test
    void shouldSaveInitialStep() {
        // arrange
        when(setupService.saveInitialStep()).thenReturn(new SimpleResponseDto(true));

        // act
        SimpleResponseDto response = controller.saveInitialStep();

        // assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
    }

    @Test
    void shouldSaveSystemProperties() {
        // arrange
        when(setupService.saveSystemProperties(any())).thenReturn(new SimpleResponseDto(true));

        // act
        SimpleResponseDto response = controller.saveSystemProperties(null);

        // assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
    }

    @Test
    void shouldSaveOrganizationData() {
        // arrange
        when(setupService.saveOrganizationData(any())).thenReturn(new SimpleResponseDto(true));

        // act
        SimpleResponseDto response = controller.saveOrganizationData(null);

        // assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
    }

    @Test
    void shouldCompleteSetup() {
        // arrange
        when(setupService.completeSetup()).thenReturn(new SimpleResponseDto(true));

        // act
        SimpleResponseDto response = controller.completeSetup();

        // assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
    }

    @Test
    void adminRoleMissing() {
        // arrange
        SaveUserRequestDto dto = new SaveUserRequestDto();
        dto.setRole(null);

        SaveEntityResponseDto mockResponse = new SaveEntityResponseDto(1L);
        when(setupService.saveAdminUser(dto)).thenReturn(mockResponse);

        // act
        SaveEntityResponseDto response = controller.saveAdminUser(dto);

        // arrange
        assertNotNull(response);
        assertEquals("setup", MDC.get(MdcParameter.USER_NAME.getDisplayName()));
        assertEquals("0", MDC.get(MdcParameter.USER_ID.getDisplayName()));

        ArgumentCaptor<SaveUserRequestDto> argumentCaptorDto = ArgumentCaptor.forClass(SaveUserRequestDto.class);
        verify(setupService).saveAdminUser(argumentCaptorDto.capture());
        assertNull(argumentCaptorDto.getValue().getRole());
    }

    @Test
    void shouldHaveRole() {
        // arrange
        SaveUserRequestDto dto = new SaveUserRequestDto();
        dto.setRole(UserRole.ROLE_USER);

        SaveEntityResponseDto mockResponse = new SaveEntityResponseDto(1L);
        when(setupService.saveAdminUser(dto)).thenReturn(mockResponse);

        // act
        SaveEntityResponseDto response = controller.saveAdminUser(dto);

        // arrange
        assertNotNull(response);
        assertEquals("setup", MDC.get(MdcParameter.USER_NAME.getDisplayName()));
        assertEquals("0", MDC.get(MdcParameter.USER_ID.getDisplayName()));

        ArgumentCaptor<SaveUserRequestDto> argumentCaptorDto = ArgumentCaptor.forClass(SaveUserRequestDto.class);
        verify(setupService).saveAdminUser(argumentCaptorDto.capture());
        assertEquals(UserRole.ROLE_ADMIN, argumentCaptorDto.getValue().getRole());
    }
}