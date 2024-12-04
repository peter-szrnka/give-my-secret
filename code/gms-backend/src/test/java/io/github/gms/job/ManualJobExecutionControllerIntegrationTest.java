package io.github.gms.job;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.common.TestedClass;
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
class ManualJobExecutionControllerIntegrationTest extends AbstractIntegrationTest {

    @Override
    @BeforeEach
    public void setup() {
        gmsUser = TestUtils.createGmsAdminUser();
        jwt = jwtService.generateJwt(TestUtils.createJwtUserRequest(gmsUser));
    }

    @Test
    void eventMaintenance_whenInputIsValid_thenReturnOk() {
        assertByUrl(ManualJobExecutionController.EVENT_MAINTENANCE);
    }

    @Test
    void generatedKeystoreCleanup_whenInputIsValid_thenReturnOk() {
        assertByUrl(ManualJobExecutionController.GENERATED_KEYSTORE_CLEANUP);
    }

    @Test
    void jobMaintenance_whenInputIsValid_thenReturnOk() {
        assertByUrl(ManualJobExecutionController.JOB_MAINTENANCE);
    }

    @Test
    void messageCleanup_whenInputIsValid_thenReturnOk() {
        assertByUrl(ManualJobExecutionController.MESSAGE_CLEANUP);
    }

    @Test
    void secretRotation_whenInputIsValid_thenReturnOk() {
        assertByUrl(ManualJobExecutionController.SECRET_ROTATION);
    }

    @Test
    void userAnonymization_whenInputIsValid_thenReturnOk() {
        assertByUrl(ManualJobExecutionController.USER_ANONYMIZATION);
    }

    @Test
    void userDeletion_whenInputIsValid_thenReturnOk() {
        assertByUrl(ManualJobExecutionController.USER_DELETION);
    }

    @Test
    void ldapUserSync_whenInputIsValid_thenReturnOk() {
        assertByUrl(ManualJobExecutionController.LDAP_USER_SYNC, HttpStatus.NOT_FOUND);
    }

    private void assertByUrl(String url) {
        assertByUrl(url, HttpStatus.OK);
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
