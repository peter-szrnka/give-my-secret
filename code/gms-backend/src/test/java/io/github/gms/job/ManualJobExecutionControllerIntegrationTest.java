package io.github.gms.job;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.abstraction.GmsControllerIntegrationTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static io.github.gms.util.TestConstants.TAG_INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_INTEGRATION_TEST)
@TestedClass(ManualJobExecutionController.class)
class ManualJobExecutionControllerIntegrationTest extends AbstractIntegrationTest implements GmsControllerIntegrationTest {

    @Override
    @BeforeEach
    public void setup() {
        gmsUser = TestUtils.createGmsAdminUser();
        jwt = jwtService.generateJwt(TestUtils.createJwtUserRequest(gmsUser));
    }

    @Test
    @TestedMethod("runJobByName")
    void runJobByName_whenInputIsValid_thenReturnOk() {
        assertByUrl("/event_maintenance", HttpStatus.OK);
    }

    @Test
    @TestedMethod("runJobByName")
    void runJobByName_whenInputIsInvalid_thenReturnNotFound() {
        assertByUrl("/invalid_job", HttpStatus.NOT_FOUND);
    }

    @Test
    @TestedMethod("runJobByName")
    void rubJobByName_whenLdapUserSync_thenReturnNotFound() {
        assertByUrl("/ldap_user_sync", HttpStatus.NOT_FOUND);
    }

    private void assertByUrl(String url, HttpStatus expectedStatus) {
        // arrange
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // act
        String path = "/secure/job_execution";
        ResponseEntity<Void> response = executeHttpGet(path + url, requestEntity, Void.class);

        // assert
        assertNotNull(response);
        assertEquals(expectedStatus, response.getStatusCode());
    }
}
