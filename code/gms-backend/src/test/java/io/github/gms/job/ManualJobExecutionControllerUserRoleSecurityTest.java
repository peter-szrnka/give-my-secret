package io.github.gms.job;

import io.github.gms.abstraction.AbstractUserRoleSecurityTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static io.github.gms.job.ManualJobExecutionController.*;
import static io.github.gms.util.TestConstants.TAG_SECURITY_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_SECURITY_TEST)
@TestedClass(ManualJobExecutionController.class)
class ManualJobExecutionControllerUserRoleSecurityTest extends AbstractUserRoleSecurityTest {

    public ManualJobExecutionControllerUserRoleSecurityTest() {
        super("/secure/job_execution");
    }

    @Test
    @TestedMethod("eventMaintenance")
    void eventMaintenance_whenUserIsNull_thenReturnHttp403() {
        assertByUrl(EVENT_MAINTENANCE);
    }

    @Test
    @TestedMethod("generatedKeystoreCleanup")
    void generatedKeystoreCleanup_whenUserIsNull_thenReturnHttp403() {
        assertByUrl(GENERATED_KEYSTORE_CLEANUP);
    }

    @Test
    @TestedMethod("jobMaintenance")
    void jobMaintenance_whenUserIsNull_thenReturnHttp403() {
        assertByUrl(JOB_MAINTENANCE);
    }

    @Test
    @TestedMethod("messageCleanup")
    void messageCleanup_whenUserIsNull_thenReturnHttp403() {
        assertByUrl(MESSAGE_CLEANUP);
    }

    @Test
    @TestedMethod("secretRotation")
    void secretRotation_whenUserIsNull_thenReturnHttp403() {
        assertByUrl(SECRET_ROTATION);
    }

    @Test
    @TestedMethod("userAnonymization")
    void userAnonymization_whenUserIsNull_thenReturnHttp403() {
        assertByUrl(USER_ANONYMIZATION);
    }

    @Test
    @TestedMethod("userDeletion")
    void userDeletion_whenUserIsNull_thenReturnHttp403() {
        assertByUrl(USER_DELETION);
    }

    @Test
    @TestedMethod("ldapUserSync")
    void ldapUserSync_whenUserIsNull_thenReturnHttp403() {
        assertByUrl(LDAP_USER_SYNC);
    }

    private void assertByUrl(String url) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // act
        ResponseEntity<Void> response = executeHttpGet(urlPrefix + url, requestEntity, Void.class);

        // assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
