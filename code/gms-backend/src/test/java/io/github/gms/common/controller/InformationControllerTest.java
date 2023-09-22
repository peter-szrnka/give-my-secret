package io.github.gms.common.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.gms.secure.dto.UserInfoDto;
import io.github.gms.secure.service.UserService;
import io.github.gms.util.TestUtils;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class InformationControllerTest {

    private UserService userService;
    private InformationController controller;
    
    @BeforeEach()
    void setup() {
        userService = mock(UserService.class);
        controller = new InformationController(userService);
    }

    @Test
    void shouldReturnHttp200() {
        // arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(userService.getUserInfo(request)).thenReturn(TestUtils.createUserInfoDto());

        // act
        UserInfoDto response = controller.getUserInfo(request);

        // assert
        assertNotNull(response);
        verify(userService).getUserInfo(request);
    }
}
