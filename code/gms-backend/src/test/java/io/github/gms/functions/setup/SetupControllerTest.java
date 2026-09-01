package io.github.gms.functions.setup;

import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.dto.SimpleResponseDto;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.SystemStatus;
import io.github.gms.common.enums.UserRole;
import io.github.gms.common.util.ThreadLocalContext;
import io.github.gms.functions.user.SaveUserRequestDto;
import io.github.gms.functions.user.UserDto;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void stepBack_whenDataSaved_thenProceed() {
        // given
        when(setupService.stepBack()).thenReturn(SystemStatus.NEED_SETUP.name());

        // when
        String response = controller.stepBack();

        // then
        assertEquals(SystemStatus.NEED_SETUP.name(), response);
        verify(setupService).stepBack();
    }

    @Test
    void getCurrentSuperAdmin_whenDataSaved_thenProceed() {
        // given
        when(setupService.getCurrentSuperAdmin()).thenReturn(TestUtils.createUserDto());

        // when
        UserDto response = controller.getCurrentSuperAdmin();

        // then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(setupService).getCurrentSuperAdmin();
    }

    @Test
    void saveInitialStep_whenDataSaved_thenProceed() {
        // given
        when(setupService.saveInitialStep()).thenReturn(new SimpleResponseDto(true));

        // when
        SimpleResponseDto response = controller.saveInitialStep();

        // then
        assertNotNull(response);
        assertTrue(response.isSuccess());
    }

    @Test
    void saveSystemProperties_whenDataSaved_thenProceed() {
        // given
        when(setupService.saveSystemProperties(any())).thenReturn(new SimpleResponseDto(true));

        // when
        SimpleResponseDto response = controller.saveSystemProperties(null);

        // then
        assertNotNull(response);
        assertTrue(response.isSuccess());
    }

    @Test
    void saveOrganizationData_whenOrgDataSaved_thenProceed() {
        // given
        when(setupService.saveOrganizationData(any())).thenReturn(new SimpleResponseDto(true));

        // when
        SimpleResponseDto response = controller.saveOrganizationData(null);

        // then
        assertNotNull(response);
        assertTrue(response.isSuccess());
    }

    @Test
    void completeSetup_whenCompletionFinished_thenSucceed() {
        // given
        when(setupService.completeSetup()).thenReturn(new SimpleResponseDto(true));

        // when
        SimpleResponseDto response = controller.completeSetup();

        // then
        assertNotNull(response);
        assertTrue(response.isSuccess());
    }

    @Test
    void saveAdminUser_whenUserRoleMissing_thenSaveUser() {
        // given
        SaveUserRequestDto dto = new SaveUserRequestDto();
        dto.setRole(null);

        SaveEntityResponseDto mockResponse = new SaveEntityResponseDto(1L);
        when(setupService.saveAdminUser(dto)).thenReturn(mockResponse);

        // when
        SaveEntityResponseDto response = controller.saveAdminUser(dto);

        // given
        assertNotNull(response);
        assertEquals("setup", ThreadLocalContext.getAsString(MdcParameter.USER_NAME));
        assertEquals("0", ThreadLocalContext.getAsString(MdcParameter.USER_ID));

        ArgumentCaptor<SaveUserRequestDto> argumentCaptorDto = ArgumentCaptor.forClass(SaveUserRequestDto.class);
        verify(setupService).saveAdminUser(argumentCaptorDto.capture());
        assertNull(argumentCaptorDto.getValue().getRole());
    }

    @Test
    void saveAdminUser_whenUserRoleDefined_thenSaveUser() {
        // given
        SaveUserRequestDto dto = new SaveUserRequestDto();
        dto.setRole(UserRole.ROLE_ADMIN);

        SaveEntityResponseDto mockResponse = new SaveEntityResponseDto(1L);
        when(setupService.saveAdminUser(dto)).thenReturn(mockResponse);

        // when
        SaveEntityResponseDto response = controller.saveAdminUser(dto);

        // given
        assertNotNull(response);
        assertEquals("setup", ThreadLocalContext.getAsString(MdcParameter.USER_NAME));
        assertEquals("0", ThreadLocalContext.getAsString(MdcParameter.USER_ID));

        ArgumentCaptor<SaveUserRequestDto> argumentCaptorDto = ArgumentCaptor.forClass(SaveUserRequestDto.class);
        verify(setupService).saveAdminUser(argumentCaptorDto.capture());
        assertEquals(UserRole.ROLE_ADMIN, argumentCaptorDto.getValue().getRole());
    }
}
