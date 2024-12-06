package io.github.gms.job;

import io.github.gms.abstraction.AbstractUserRoleSecurityTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.job.model.UrlConstants;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

    @ParameterizedTest
    @TestedMethod("runJobByName")
    @ValueSource(strings = {
            UrlConstants.JOB_MAINTENANCE,
            UrlConstants.EVENT_MAINTENANCE,
            UrlConstants.GENERATED_KEYSTORE_CLEANUP,
            UrlConstants.LDAP_USER_SYNC,
            UrlConstants.MESSAGE_CLEANUP,
            UrlConstants.SECRET_ROTATION,
            UrlConstants.USER_ANONYMIZATION,
            UrlConstants.USER_DELETION
    })
    void runJobByName_whenUserIsNull_thenReturnHttp403(String urlPath) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // act
        ResponseEntity<Void> response = executeHttpGet(urlPrefix + "/" + urlPath, requestEntity, Void.class);

        // assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
