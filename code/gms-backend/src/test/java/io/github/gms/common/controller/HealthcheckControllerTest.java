package io.github.gms.common.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class HealthcheckControllerTest {

    @Test
    void healthcheck_whenCalled_thenReturnOk() {
        // given
        HealthcheckController controller = new HealthcheckController();

        // when
        ResponseEntity<Void> response = controller.healthcheck();

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }
}

