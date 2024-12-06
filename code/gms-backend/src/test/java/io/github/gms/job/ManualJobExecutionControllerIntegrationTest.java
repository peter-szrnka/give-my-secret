package io.github.gms.job;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.abstraction.GmsControllerIntegrationTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.job.model.UrlConstants;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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

    @ParameterizedTest
    @TestedMethod("runJobByName")
    @MethodSource("jobNameTestData")
    void runJobByName_whenInputIsProvided_thenReturnExpectedStatus(String urlPath, HttpStatus expectedStatus) {
        assertByUrl("/" + urlPath, expectedStatus);
    }

    private static Object[][] jobNameTestData() {
        return new Object[][] {
            {UrlConstants.JOB_MAINTENANCE, HttpStatus.OK},
            {UrlConstants.EVENT_MAINTENANCE, HttpStatus.OK},
            {UrlConstants.GENERATED_KEYSTORE_CLEANUP, HttpStatus.OK},
            {UrlConstants.MESSAGE_CLEANUP, HttpStatus.OK},
            {UrlConstants.SECRET_ROTATION, HttpStatus.OK},
            {UrlConstants.USER_ANONYMIZATION, HttpStatus.OK},
            {UrlConstants.USER_DELETION, HttpStatus.OK},
            {UrlConstants.LDAP_USER_SYNC, HttpStatus.NOT_FOUND},
            {"/invalid_job", HttpStatus.NOT_FOUND}
        };
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
