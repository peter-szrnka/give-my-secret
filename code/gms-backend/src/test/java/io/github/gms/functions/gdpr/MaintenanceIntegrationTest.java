package io.github.gms.functions.gdpr;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.functions.gdpr.model.BatchUserOperationDto;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static io.github.gms.util.TestConstants.TAG_INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_INTEGRATION_TEST)
class MaintenanceIntegrationTest extends AbstractIntegrationTest {

    @Override
    @BeforeEach
    public void setup() {
        gmsUser = TestUtils.createGmsAdminUser();
        jwt = jwtService.generateJwt(TestUtils.createJwtAdminRequest(gmsUser));
    }

    @Test
    void shouldRequestUserDeletion() {
        // arrange

        // act
        BatchUserOperationDto input = BatchUserOperationDto.builder().build();
        HttpEntity<BatchUserOperationDto> requestEntity = new HttpEntity<>(input, TestUtils.getHttpHeaders(jwt));
        ResponseEntity<Void> response = executeHttpPost("/secure/maintenance/request_user_deletion", requestEntity, Void.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
