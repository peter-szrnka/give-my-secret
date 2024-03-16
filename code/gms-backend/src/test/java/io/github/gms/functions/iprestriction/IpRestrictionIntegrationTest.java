package io.github.gms.functions.iprestriction;

import io.github.gms.abstraction.AbstractClientControllerIntegrationTest;
import io.github.gms.common.dto.PagingDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import static io.github.gms.util.TestConstants.TAG_INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_INTEGRATION_TEST)
class IpRestrictionIntegrationTest extends AbstractClientControllerIntegrationTest {

	IpRestrictionIntegrationTest() {
		super("/secure/ip_restriction");
	}
	
	@Override
	@BeforeEach
	public void setup() {
		gmsUser = TestUtils.createGmsAdminUser();
		jwt = jwtService.generateJwt(TestUtils.createJwtAdminRequest(gmsUser));
	}

	@Transactional
	@Test
	void testSave() {
		// act
		HttpEntity<IpRestrictionDto> saveRequestEntity = new HttpEntity<>(
				TestUtils.createIpRestrictionDto(true),
				TestUtils.getHttpHeaders(jwt));
		ResponseEntity<SaveEntityResponseDto> saveResponse = executeHttpPost("", saveRequestEntity, SaveEntityResponseDto.class);
		
		// assert
		assertEquals(HttpStatus.OK, saveResponse.getStatusCode());
		assertNotNull(saveResponse.getBody());
		Long newUserId = saveResponse.getBody().getEntityId();
		
		// cleanup
		/*HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		executeHttpDelete("/" + newUserId, requestEntity,
				String.class);*/
	}
	
	@Test
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
	void testList() {
		// act
		PagingDto request = PagingDto.builder().page(0).size(50).direction("ASC").property("id").build();

		HttpEntity<PagingDto> requestEntity = new HttpEntity<>(request, TestUtils.getHttpHeaders(jwt));
		ResponseEntity<IpRestrictionListDto> response = executeHttpPost("/list", requestEntity, IpRestrictionListDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());

		IpRestrictionListDto responseList = response.getBody();
		assertFalse(responseList.getResultList().isEmpty());
	}
	
	@Test
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
}
