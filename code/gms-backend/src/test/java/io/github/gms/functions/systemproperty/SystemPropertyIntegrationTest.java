package io.github.gms.functions.systemproperty;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.abstraction.GmsControllerIntegrationTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.enums.SystemProperty;
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
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_INTEGRATION_TEST)
@TestedClass(SystemPropertyController.class)
class SystemPropertyIntegrationTest extends AbstractIntegrationTest implements GmsControllerIntegrationTest {
	
	@Override
	@BeforeEach
	public void setup() {
		gmsUser = TestUtils.createGmsAdminUser();
		jwt = jwtService.generateJwt(TestUtils.createJwtAdminRequest(gmsUser));
	}
	
	@Test
	void testSave() {
		// act
		HttpEntity<SystemPropertyDto> requestEntity = new HttpEntity<>(SystemPropertyDto.builder().key(SystemProperty.JOB_OLD_EVENT_LIMIT.name()).value("2;d").build(), TestUtils.getHttpHeaders(jwt));
		ResponseEntity<Void> response = executeHttpPost("/secure/system_property", requestEntity, Void.class);
		
		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
	}
	
	@Test
	void testDelete() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpDelete("/secure/system_property/" + SystemProperty.REFRESH_JWT_ALGORITHM.name(), requestEntity, String.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Test
	void testList() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<SystemPropertyListDto> response = executeHttpGet("/secure/system_property/list?page=0&size=10&direction=ASC&property=id", requestEntity, SystemPropertyListDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		SystemPropertyListDto responseList = response.getBody();
		assertEquals(SystemProperty.values().length, responseList.getResultList().size());
	}
}