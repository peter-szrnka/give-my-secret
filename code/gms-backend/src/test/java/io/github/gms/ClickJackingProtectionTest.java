package io.github.gms;

import io.github.gms.abstraction.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class ClickJackingProtectionTest extends AbstractIntegrationTest {

    @Test
    void testClickJackingProtection() {
        // arrange
        HttpEntity<Void> requestEntity = new HttpEntity<>(null);

        // act
        ResponseEntity<Void> response = executeHttpGet("/healthcheck", requestEntity, Void.class);

        // assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("DENY", Objects.requireNonNull(response.getHeaders().get("X-Frame-Options")).getFirst());
        assertEquals("frame-ancestors 'none'", Objects.requireNonNull(response.getHeaders().get("Content-Security-Policy")).getFirst());
    }
}