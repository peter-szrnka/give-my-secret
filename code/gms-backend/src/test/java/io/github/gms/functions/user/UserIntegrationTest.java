package io.github.gms.functions.user;

import io.github.gms.abstraction.AbstractClientControllerIntegrationTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.functions.apikey.ApiKeyDto;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static io.github.gms.util.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_INTEGRATION_TEST)
@TestedClass(UserController.class)
class UserIntegrationTest extends AbstractClientControllerIntegrationTest {

	UserIntegrationTest() {
		super("/secure/user");
	}
	
	@Override
	@BeforeEach
	public void setup() {
		gmsUser = TestUtils.createGmsAdminUser();
		jwt = jwtService.generateJwt(TestUtils.createJwtUserRequest(gmsUser));
	}

	@Test
	@Transactional
	@TestedMethod(SAVE)
	void save_whenInputIsValid_thenReturnOk() {
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
	@TestedMethod(GET_BY_ID)
	void getById_whenInputIsValid_thenReturnOk() {
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
	@TestedMethod(LIST)
	void list_whenInputIsValid_thenReturnOk() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<UserListDto> response = executeHttpGet("/list?page=0&size=10&direction=ASC&property=id", requestEntity, UserListDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		UserListDto responseList = response.getBody();
		assertFalse(responseList.getResultList().isEmpty());
	}
	
	@Test
	@TestedMethod(DELETE)
	void delete_whenInputIsValid_thenReturnOk() {
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
	@TestedMethod(TOGGLE)
	@ValueSource(booleans = { true, false })
	void toggle_whenInputIsValid_thenReturnOk(boolean enabled) {
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
	
	@Test
	@Transactional
	@TestedMethod("changePassword")
	void changePassword_whenInputIsValid_thenReturnOk() {
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
    @TestedMethod("getMfaQrCode")
    void getMfaQrCode_whenInputIsValid_thenReturnOk() {
        // arrange
		gmsUser = TestUtils.createGmsMfaUser();
		jwt = jwtService.generateJwt(TestUtils.createJwtUserRequest(gmsUser));
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", ACCESS_JWT_TOKEN + "=" + jwt + ";Max-Age=3600;HttpOnly");
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // act
        ResponseEntity<byte[]> response = executeHttpGet("/mfa_qr_code", requestEntity, byte[].class);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @TestedMethod("toggleMfa")
    void toggleMfa_whenInputIsValid_thenReturnOk() {
        // act
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
        ResponseEntity<Void> response = executeHttpPost("/toggle_mfa?enabled=true", requestEntity, Void.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());

        UserEntity entity = userRepository.getReferenceById(DemoData.USER_1_ID);
        assertNotNull(entity);
    }

	@Test
	@Transactional
	@TestedMethod("isMfaActive")
	void isMfaActive_whenInputIsValid_thenReturnOk() {
		// arrange
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

		// act
		ResponseEntity<Boolean> response = executeHttpGet("/mfa_active", requestEntity, Boolean.class);

		// Assert
		assertNotNull(response);
		assertNotNull(response.getBody());
		assertThat(response.getBody()).isTrue();
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
}
