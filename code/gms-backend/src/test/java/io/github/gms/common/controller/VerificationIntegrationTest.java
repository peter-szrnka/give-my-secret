package io.github.gms.common.controller;

import dev.samstevens.totp.code.CodeVerifier;
import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.abstraction.GmsControllerIntegrationTest;
import io.github.gms.auth.dto.AuthenticateResponseDto;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.common.dto.LoginVerificationRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static io.github.gms.util.TestUtils.USERNAME_MFA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@TestedClass(VerificationController.class)
public class VerificationIntegrationTest extends AbstractIntegrationTest implements GmsControllerIntegrationTest {

    @MockBean
    private CodeVerifier verifier;

    @Test
    @TestedMethod("verify")
    void shouldVerify() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        LoginVerificationRequestDto request = new LoginVerificationRequestDto();
        request.setUsername(USERNAME_MFA);
        request.setVerificationCode("123456");
        HttpEntity<LoginVerificationRequestDto> requestEntity = new HttpEntity<>(request, headers);
        when(verifier.isValidCode(anyString(), eq(request.getVerificationCode()))).thenReturn(true);

        // act
        ResponseEntity<AuthenticateResponseDto> response = executeHttpPost("/verify", requestEntity, AuthenticateResponseDto.class);

        // assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
