package io.github.gms.functions.maintenance.job;

import io.github.gms.abstraction.AbstractClientControllerIntegrationTest;
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
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_INTEGRATION_TEST)
@TestedClass(JobMaintenanceController.class)
class JobMaintenanceIntegrationTest extends AbstractClientControllerIntegrationTest {

	JobMaintenanceIntegrationTest() {
		super("/secure/job");
	}

	@Override
	@BeforeEach
	public void setup() {
		gmsUser = TestUtils.createGmsAdminUser();
		jwt = jwtService.generateJwt(TestUtils.createJwtUserRequest(gmsUser));
	}

	@Test
	@TestedMethod("list")
	void testList() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<JobListDto> response = executeHttpGet("/list?page=0&size=10&direction=ASC&property=id", requestEntity, JobListDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertFalse(response.getBody().getResultList().isEmpty());
	}
}
