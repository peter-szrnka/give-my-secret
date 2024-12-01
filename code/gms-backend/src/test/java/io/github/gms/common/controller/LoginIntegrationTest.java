package io.github.gms.common.controller;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.abstraction.GmsControllerIntegrationTest;
import io.github.gms.auth.dto.AuthenticateRequestDto;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static io.github.gms.common.controller.LoginController.LOGIN_PATH;
import static io.github.gms.common.controller.LoginController.LOGOUT_PATH;
import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@TestedClass(LoginController.class)
class LoginIntegrationTest extends AbstractIntegrationTest implements GmsControllerIntegrationTest {

    @Test
    @TestedMethod("loginAuthentication")
    void login_whenValidCredentialsSent_thenLogInUser() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        // act
        HttpEntity<AuthenticateRequestDto> requestEntity = new HttpEntity<>(
                new AuthenticateRequestDto("username1", "test"), headers);
        ResponseEntity<Void> response = executeHttpPost("/" + LOGIN_PATH, requestEntity, Void.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @TestedMethod("logout")
    void logout_whenCookiesProvided_thenLogOut() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", ACCESS_JWT_TOKEN + "=" + jwt);

        // act
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<Void> response = executeHttpPost("/" + LOGOUT_PATH, requestEntity, Void.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
