package io.github.gms.common.controller;

import io.github.gms.auth.VerificationService;
import io.github.gms.auth.dto.AuthenticateResponseDto;
import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.auth.types.AuthResponsePhase;
import io.github.gms.common.dto.LoginVerificationRequestDto;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test of {@link VerificationController}
 *
 * @author Peter Szrnka
 */
@ExtendWith(MockitoExtension.class)
class VerificationControllerTest {

    private VerificationService service;
    private SystemPropertyService systemPropertyService;
    private VerificationController controller;

    @BeforeEach
    void setup() {
        service = mock(VerificationService.class);
        systemPropertyService = mock(SystemPropertyService.class);
        controller = new VerificationController(service, systemPropertyService, false);
    }


    @Test
    void shouldVerify() {
        // arrange
        LoginVerificationRequestDto dto = new LoginVerificationRequestDto();
        UserInfoDto userInfoDto = TestUtils.createUserInfoDto();
        AuthenticationResponse mockResponse = new AuthenticationResponse();
        mockResponse.setCurrentUser(userInfoDto);
        mockResponse.setPhase(AuthResponsePhase.COMPLETED);
        mockResponse.setToken("token");
        when(service.verify(dto)).thenReturn(mockResponse);
        when(systemPropertyService.getLong(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS)).thenReturn(2L);

        // act
        ResponseEntity<AuthenticateResponseDto> response = controller.verify(dto);

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(userInfoDto, Objects.requireNonNull(response.getBody()).getCurrentUser());
        assertEquals(AuthResponsePhase.COMPLETED, response.getBody().getPhase());
        verify(service).verify(dto);
        verify(systemPropertyService).getLong(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
    }

    @Test
    void shouldVerifyFail() {
        // arrange
        LoginVerificationRequestDto dto = new LoginVerificationRequestDto();
        UserInfoDto userInfoDto = TestUtils.createUserInfoDto();
        AuthenticationResponse mockResponse = new AuthenticationResponse();
        mockResponse.setCurrentUser(userInfoDto);
        mockResponse.setPhase(AuthResponsePhase.FAILED);
        when(service.verify(dto)).thenReturn(mockResponse);

        // act
        ResponseEntity<AuthenticateResponseDto> response = controller.verify(dto);

        // assert
        assertNotNull(response);
        assertEquals(401, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(service).verify(dto);
    }
}
