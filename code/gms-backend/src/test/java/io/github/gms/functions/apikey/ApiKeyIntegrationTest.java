package io.github.gms.functions.apikey;

import io.github.gms.abstraction.AbstractClientControllerIntegrationTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.common.dto.IdNamePairListDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.functions.secret.GetSecureValueDto;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import static io.github.gms.util.TestConstants.TAG_INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_INTEGRATION_TEST)
@TestedClass(ApiKeyController.class)
class ApiKeyIntegrationTest extends AbstractClientControllerIntegrationTest {

	ApiKeyIntegrationTest() {
		super("/secure/apikey");
	}

	@Test
	@TestedMethod("save")
	void save_whenInputIsValid_thenReturnOk() {
		// act
		HttpEntity<SaveApiKeyRequestDto> requestEntity = new HttpEntity<>(TestUtils.createSaveApiKeyRequestDto(), TestUtils.getHttpHeaders(jwt));
		ResponseEntity<SaveEntityResponseDto> response = executeHttpPost("", requestEntity, SaveEntityResponseDto.class);
		
		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		SaveEntityResponseDto responseBody = response.getBody();
		assertTrue(responseBody.isSuccess());
	}
	
	@Test
	@TestedMethod("getById")
	void getById_whenInputIsValid_thenReturnOk() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<ApiKeyDto> response = executeHttpGet("/" + DemoData.API_KEY_1_ID, requestEntity, ApiKeyDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		ApiKeyDto responseBody = response.getBody();
		assertEquals(DemoData.API_KEY_1_ID, responseBody.getId());
		assertEquals(EntityStatus.ACTIVE, responseBody.getStatus());
		assertEquals(DemoData.API_KEY_CREDENTIAL1, responseBody.getValue());
	}
	
	@Test
	@TestedMethod("list")
	void list_whenInputIsValid_thenReturnOk() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<ApiKeyListDto> response = executeHttpGet("/list?page=0&size=10&direction=ASC&property=id", requestEntity, ApiKeyListDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertFalse(response.getBody().getResultList().isEmpty());
	}
	
	@Test
	@TestedMethod("getValue")
	void getValue_whenInputIsValid_thenReturnOk() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpGet("/value/" + DemoData.API_KEY_1_ID, requestEntity, String.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		String responseBody = response.getBody();
		assertEquals(DemoData.API_KEY_CREDENTIAL1, responseBody);
	}
	
	@Test
	@TestedMethod("delete")
	void delete_whenInputIsValid_thenReturnOk() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpDelete("/" + DemoData.API_KEY_2_ID, requestEntity,
				String.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Transactional
	@ParameterizedTest
	@TestedMethod("toggle")
	@ValueSource(booleans = { false, true })
	void toggle_whenInputIsValid_thenReturnOk(boolean enabled) {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpPost("/" + DemoData.API_KEY_1_ID + "?enabled="+ enabled, requestEntity,
				String.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
		
		ApiKeyEntity entity = apiKeyRepository.getReferenceById(DemoData.API_KEY_1_ID);
		assertNotNull(entity);
		assertEquals(enabled ? EntityStatus.ACTIVE : EntityStatus.DISABLED, entity.getStatus());

		executeHttpPost("/" + DemoData.API_KEY_1_ID + "?enabled="+ !enabled, requestEntity,String.class);
	}
	
	
	@Test
	@TestedMethod("getAllApiKeyNames")
	void getAllApiKeyNames_whenInputIsValid_thenReturnOk() {
		HttpEntity<GetSecureValueDto> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		
		// act
		ResponseEntity<IdNamePairListDto> response = executeHttpGet("/list_names", requestEntity, IdNamePairListDto.class);
		
		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertFalse(response.getBody().getResultList().isEmpty());
		assertFalse(response.getBody().getResultList().isEmpty());
	}
}
