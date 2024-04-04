package io.github.gms.common.controller;

import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.functions.user.UserInfoService;
import io.github.gms.util.TestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class InformationControllerTest {

    private UserInfoService userInfoService;
    private InformationController controller;
    
    @BeforeEach()
    void setup() {
        userInfoService = mock(UserInfoService.class);
        controller = new InformationController(userInfoService);
    }

    @Test
    void shouldReturnHttp200() {
        // arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(userInfoService.getUserInfo(request)).thenReturn(TestUtils.createUserInfoDto());

        // act
        UserInfoDto response = controller.getUserInfo(request);

        // assert
        assertNotNull(response);
        verify(userInfoService).getUserInfo(request);
    }
}
