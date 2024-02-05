package io.github.gms.common.controller;

import io.github.gms.common.dto.ResetPasswordRequestDto;
import io.github.gms.common.service.ResetPasswordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

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

        // act
        ResponseEntity<Void> response = controller.resetPassword(new ResetPasswordRequestDto("test"));

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }
}
