package io.github.gms.common.controller;

import io.github.gms.functions.resetpassword.ResetPasswordRequestDto;
import io.github.gms.functions.resetpassword.ResetPasswordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class ResetPasswordControllerTest {

    @Test
    void shouldReturnOk() {
        // arrange
        ResetPasswordService service = mock(ResetPasswordService.class);
        ResetPasswordController controller = new ResetPasswordController(service);
        ResetPasswordRequestDto dto = new ResetPasswordRequestDto("test");

        // act
        ResponseEntity<Void> response = controller.resetPassword(dto);

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(service).resetPassword(dto);
    }
}
