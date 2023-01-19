package io.github.gms.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import io.github.gms.common.util.DemoDataProviderService;
import io.github.gms.secure.dto.ApiKeyDto;
import io.github.gms.secure.dto.ApiKeyListDto;
import io.github.gms.secure.dto.GetSecureValueDto;
import io.github.gms.secure.dto.IdNamePairListDto;
import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveApiKeyRequestDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.entity.ApiKeyEntity;
import io.github.gms.util.TestConstants;
import io.github.gms.util.TestUtils;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TestConstants.TAG_INTEGRATION_TEST)
class ApiKeyIntegrationTest extends AbstractClientControllerIntegrationTest {

	ApiKeyIntegrationTest() {
		super("/secure/apikey");
	}

	@Test
	void testSave() {
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
	void testGetById() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<ApiKeyDto> response = executeHttpGet("/" + DemoDataProviderService.API_KEY_1_ID, requestEntity, ApiKeyDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		ApiKeyDto responseBody = response.getBody();
		assertEquals(DemoDataProviderService.API_KEY_1_ID, responseBody.getId());
		assertEquals(EntityStatus.ACTIVE, responseBody.getStatus());
		assertEquals(DemoDataProviderService.API_KEY_CREDENTIAL1, responseBody.getValue());
	}
	
	@Test
	void testList() {
		// act
		PagingDto request = PagingDto.builder().page(0).size(50).direction("ASC").property("id").build();

		HttpEntity<PagingDto> requestEntity = new HttpEntity<>(request, TestUtils.getHttpHeaders(jwt));
		ResponseEntity<ApiKeyListDto> response = executeHttpPost("/list", requestEntity, ApiKeyListDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		ApiKeyListDto responseList = response.getBody();
		assertEquals(1, responseList.getResultList().size());
	}
	
	@Test
	void testGetValue() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpGet("/value/" + DemoDataProviderService.API_KEY_1_ID, requestEntity, String.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		String responseBody = response.getBody();
		assertEquals(DemoDataProviderService.API_KEY_CREDENTIAL1, responseBody);
	}
	
	@Test
	void testDelete() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpDelete("/" + DemoDataProviderService.API_KEY_2_ID, requestEntity,
				String.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Transactional
	@ParameterizedTest
	@ValueSource(booleans = { false, true })
	void testToggleStatus(boolean enabled) {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpPost("/" + DemoDataProviderService.API_KEY_1_ID + "?enabled="+ enabled, requestEntity,
				String.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
		
		ApiKeyEntity entity = apiKeyRepository.getReferenceById(DemoDataProviderService.API_KEY_1_ID);
		assertNotNull(entity);
		assertEquals(enabled ? EntityStatus.ACTIVE : EntityStatus.DISABLED, entity.getStatus());

		executeHttpPost("/" + DemoDataProviderService.API_KEY_1_ID + "?enabled="+ !enabled, requestEntity,String.class);
	}

	@Test
	void testCount() {
		HttpEntity<GetSecureValueDto> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		// act
		ResponseEntity<LongValueDto> response = executeHttpGet("/count", requestEntity, LongValueDto.class);
		
		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(1L, response.getBody().getValue());
	}
	
	
	@Test
	void testGetAllApiKeyNames() {
		HttpEntity<GetSecureValueDto> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		
		// act
		ResponseEntity<IdNamePairListDto> response = executeHttpGet("/list_names", requestEntity, IdNamePairListDto.class);
		
		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertFalse(response.getBody().getResultList().isEmpty());
		assertEquals(1, response.getBody().getResultList().size());
	}
}
