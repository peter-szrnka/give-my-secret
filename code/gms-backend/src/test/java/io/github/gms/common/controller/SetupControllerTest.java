package io.github.gms.common.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.UserRole;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.dto.SaveUserRequestDto;
import io.github.gms.secure.service.UserService;

/**
 * Unit test of {@link SetupController}
 * 
 * @author Peter Szrnka
 */
@ExtendWith(MockitoExtension.class)
class SetupControllerTest {
    
    private SetupController controller;
    private UserService userService;

    @BeforeEach
    void setup() {
        userService = mock(UserService.class);
        controller = new SetupController(userService);
    }

    @Test
    void adminRoleMissing() {
        // arrange
        SaveUserRequestDto dto = new SaveUserRequestDto();
        dto.setRoles(Set.of());

        SaveEntityResponseDto mockResponse = new SaveEntityResponseDto(1L);
        when(userService.saveAdminUser(dto)).thenReturn(mockResponse);

        // act
        SaveEntityResponseDto response = controller.saveAdminUser(dto);

        // arrange
        assertNotNull(response);
        assertEquals("setup", MDC.get(MdcParameter.USER_NAME.getDisplayName()));
        assertEquals("0", MDC.get(MdcParameter.USER_ID.getDisplayName()));

        ArgumentCaptor<SaveUserRequestDto> argumentCaptorDto = ArgumentCaptor.forClass(SaveUserRequestDto.class);
        verify(userService).saveAdminUser(argumentCaptorDto.capture());
        assertEquals(UserRole.ROLE_ADMIN, argumentCaptorDto.getValue().getRoles().iterator().next());
    }

    @Test
    void shouldHaveRole() {
        // arrange
        SaveUserRequestDto dto = new SaveUserRequestDto();
        dto.setRoles(Set.of(UserRole.ROLE_USER));

        SaveEntityResponseDto mockResponse = new SaveEntityResponseDto(1L);
        when(userService.saveAdminUser(dto)).thenReturn(mockResponse);

        // act
        SaveEntityResponseDto response = controller.saveAdminUser(dto);

        // arrange
        assertNotNull(response);
        assertEquals("setup", MDC.get(MdcParameter.USER_NAME.getDisplayName()));
        assertEquals("0", MDC.get(MdcParameter.USER_ID.getDisplayName()));

        ArgumentCaptor<SaveUserRequestDto> argumentCaptorDto = ArgumentCaptor.forClass(SaveUserRequestDto.class);
        verify(userService).saveAdminUser(argumentCaptorDto.capture());
        assertEquals(UserRole.ROLE_USER, argumentCaptorDto.getValue().getRoles().iterator().next());
    }
}