package io.github.gms.common.controller;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.abstraction.GmsControllerIntegrationTest;
import io.github.gms.auth.dto.AuthenticateResponseDto;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.common.dto.LoginVerificationRequestDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Disabled("TODO Fix this test!")
@TestedClass(value = VerificationController.class, skip = true) // TODO Fix this test!
public class VerificationIntegrationTest extends AbstractIntegrationTest implements GmsControllerIntegrationTest {

    @Test
    @TestedMethod("verify")
    void shouldVerify() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<LoginVerificationRequestDto> requestEntity = new HttpEntity<>(new LoginVerificationRequestDto(), headers);

        // act
        ResponseEntity<AuthenticateResponseDto> response = executeHttpPost("/verify", requestEntity, AuthenticateResponseDto.class);

        // assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
