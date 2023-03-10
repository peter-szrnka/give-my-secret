package io.github.gms.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import io.github.gms.abstraction.AbstractClientControllerIntegrationTest;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.secure.dto.ApiKeyDto;
import io.github.gms.secure.dto.ChangePasswordRequestDto;
import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.dto.SaveUserRequestDto;
import io.github.gms.secure.dto.UserListDto;
import io.github.gms.secure.entity.UserEntity;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestConstants;
import io.github.gms.util.TestUtils;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TestConstants.TAG_INTEGRATION_TEST)
class UserIntegrationTest extends AbstractClientControllerIntegrationTest {

	UserIntegrationTest() {
		super("/secure/user");
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
		HttpEntity<SaveUserRequestDto> saveRequestEntity = new HttpEntity<>(
				TestUtils.createSaveUserRequestDto(null, "user3", "user3@user3.com"), 
				TestUtils.getHttpHeaders(jwt));
		ResponseEntity<SaveEntityResponseDto> saveResponse = executeHttpPost("", saveRequestEntity, SaveEntityResponseDto.class);
		
		// assert
		assertEquals(HttpStatus.OK, saveResponse.getStatusCode());
		assertNotNull(saveResponse.getBody());
		Long newUserId = saveResponse.getBody().getEntityId();
		
		// cleanup
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		executeHttpDelete("/" + newUserId, requestEntity,
				String.class);
	}
	
	@Test
	void testGetById() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<ApiKeyDto> response = executeHttpGet("/" + DemoData.USER_1_ID, requestEntity, ApiKeyDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		ApiKeyDto responseBody = response.getBody();
		assertEquals(DemoData.USER_1_ID, responseBody.getId());
		assertEquals(EntityStatus.ACTIVE, responseBody.getStatus());
		assertNull(responseBody.getValue());
	}
	
	@Test
	void testList() {
		// act
		PagingDto request = PagingDto.builder().page(0).size(50).direction("ASC").property("id").build();

		HttpEntity<PagingDto> requestEntity = new HttpEntity<>(request, TestUtils.getHttpHeaders(jwt));
		ResponseEntity<UserListDto> response = executeHttpPost("/list", requestEntity, UserListDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		UserListDto responseList = response.getBody();
		assertFalse(responseList.getResultList().isEmpty());
	}
	
	@Test
	void testDelete() {
		// arrange
		HttpEntity<SaveUserRequestDto> saveRequestEntity = new HttpEntity<>(
				TestUtils.createSaveUserRequestDto(null, "user3", "user3@user3.com"), 
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
	@ValueSource(booleans = { true, false })
	void testToggleStatus(boolean enabled) {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpPost("/" + DemoData.USER_1_ID + "?enabled="+ enabled, requestEntity,
				String.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
		
		UserEntity entity = userRepository.getReferenceById(DemoData.USER_1_ID);
		assertNotNull(entity);
		assertEquals(enabled ? EntityStatus.ACTIVE : EntityStatus.DISABLED, entity.getStatus());

		executeHttpPost("/" + DemoData.USER_1_ID + "?enabled="+ !enabled, requestEntity,String.class);
	}
	
	@Transactional
	@Test
	void testChangePassword() {
		// arrange
		HttpEntity<ChangePasswordRequestDto> requestEntity = new HttpEntity<>(TestUtils.createChangePasswordRequestDto(), TestUtils.getHttpHeaders(jwt));

		// act
		ResponseEntity<Void> response = executeHttpPost("/change_credential", requestEntity, Void.class);
		
		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
		
		UserEntity entity = userRepository.getReferenceById(DemoData.USER_1_ID);
		assertNotNull(entity);
	}
	
	@Test
	void testUserCount() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<LongValueDto> response = executeHttpGet("/count", requestEntity, LongValueDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		LongValueDto responseBody = response.getBody();
		assertNotEquals(0L, responseBody.getValue());
	}
}
