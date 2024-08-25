package io.github.gms.common.controller;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.abstraction.GmsControllerIntegrationTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.functions.resetpassword.ResetPasswordRequestDto;
import io.github.gms.util.DemoData;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@TestedClass(ResetPasswordController.class)
public class ResetPasswordIntegrationTest extends AbstractIntegrationTest implements GmsControllerIntegrationTest {

    @Test
    @TestedMethod(value = "resetPassword")
    void shouldResetPassword() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        ResetPasswordRequestDto dto = new ResetPasswordRequestDto();
        dto.setUsername(DemoData.USERNAME1);
        HttpEntity<ResetPasswordRequestDto> requestEntity = new HttpEntity<>(dto, headers);

        // act
        ResponseEntity<Void> response = executeHttpPost("/reset_password", requestEntity, Void.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
