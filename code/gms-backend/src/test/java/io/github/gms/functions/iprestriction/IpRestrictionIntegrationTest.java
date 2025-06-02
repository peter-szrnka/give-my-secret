package io.github.gms.functions.iprestriction;

import io.github.gms.abstraction.AbstractClientControllerIntegrationTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import static io.github.gms.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_INTEGRATION_TEST)
@TestedClass(IpRestrictionController.class)
class IpRestrictionIntegrationTest extends AbstractClientControllerIntegrationTest {

	IpRestrictionIntegrationTest() {
		super("/secure/ip_restriction");
	}
	
	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		gmsUser = TestUtils.createGmsAdminUser();
		jwt = jwtService.generateJwt(TestUtils.createJwtUserRequest(gmsUser));
	}

	@Test
	@Transactional
	@TestedMethod(SAVE)
	void testSave() {
		// act
		HttpEntity<IpRestrictionDto> saveRequestEntity = new HttpEntity<>(
				TestUtils.createIpRestrictionDto(true),
				TestUtils.getHttpHeaders(jwt));
		ResponseEntity<SaveEntityResponseDto> saveResponse = executeHttpPost("", saveRequestEntity, SaveEntityResponseDto.class);
		
		// assert
		assertEquals(HttpStatus.OK, saveResponse.getStatusCode());
		assertNotNull(saveResponse.getBody());
	}
	
	@Test
	@TestedMethod(GET_BY_ID)
	void testGetById() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<IpRestrictionDto> response = executeHttpGet("/1", requestEntity, IpRestrictionDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());

		IpRestrictionDto responseBody = response.getBody();
		assertEquals(DemoData.USER_1_ID, responseBody.getId());
	}
	
	@Test
	@TestedMethod(LIST)
	void testList() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<IpRestrictionListDto> response = executeHttpGet("/list?page=0&size=10&direction=ASC&property=id", requestEntity, IpRestrictionListDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());

		IpRestrictionListDto responseList = response.getBody();
		assertFalse(responseList.getResultList().isEmpty());
	}
	
	@Test
	@TestedMethod(DELETE)
	void testDelete() {
		// arrange
		HttpEntity<IpRestrictionDto> saveRequestEntity = new HttpEntity<>(
				TestUtils.createIpRestrictionDto(true),
				TestUtils.getHttpHeaders(jwt));
		ResponseEntity<SaveEntityResponseDto> saveResponse = executeHttpPost("", saveRequestEntity, SaveEntityResponseDto.class);
		assertEquals(HttpStatus.OK, saveResponse.getStatusCode());
		assertNotNull(saveResponse.getBody());
		Long newUserId = saveResponse.getBody().getEntityId();

		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpDelete("/" + newUserId, requestEntity,
				String.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Transactional
	@ParameterizedTest
	@TestedMethod(TOGGLE)
	@ValueSource(booleans = { false, true })
	void testToggleStatus(boolean enabled) {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpPost("/1?enabled="+ enabled, requestEntity,
				String.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());

		executeHttpPost("/1?enabled="+ !enabled, requestEntity,String.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
	}
}
