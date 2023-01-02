package io.github.gms.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
import io.github.gms.common.entity.SecretEntity;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.util.DemoDataProviderService;
import io.github.gms.secure.dto.GetSecureValueDto;
import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.dto.SaveSecretRequestDto;
import io.github.gms.secure.dto.SecretDto;
import io.github.gms.secure.dto.SecretListDto;
import io.github.gms.util.TestConstants;
import io.github.gms.util.TestUtils;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TestConstants.TAG_INTEGRATION_TEST)
class SecretIntegrationTest extends AbstractClientControllerIntegrationTest {

	SecretIntegrationTest() {
		super("/secure/secret");
	}

	@Test
	void testSave() {
		// act
		HttpEntity<SaveSecretRequestDto> requestEntity = new HttpEntity<>(
				TestUtils.createSaveSecretRequestDto(DemoDataProviderService.SECRET_ENTITY_ID),
				TestUtils.getHttpHeaders(jwt));
		ResponseEntity<SaveEntityResponseDto> response = executeHttpPost("", requestEntity,
				SaveEntityResponseDto.class);

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
		ResponseEntity<SecretDto> response = executeHttpGet("/" + DemoDataProviderService.SECRET_ENTITY_ID,
				requestEntity, SecretDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());

		SecretDto responseBody = response.getBody();
		assertEquals(DemoDataProviderService.SECRET_ENTITY_ID, responseBody.getId());
		assertEquals(EntityStatus.ACTIVE, responseBody.getStatus());
	}

	@Test
	void testList() {
		// act
		PagingDto request = PagingDto.builder().page(0).size(50).direction("ASC").property("id").build();

		HttpEntity<PagingDto> requestEntity = new HttpEntity<>(request, TestUtils.getHttpHeaders(jwt));
		ResponseEntity<SecretListDto> response = executeHttpPost("/list", requestEntity, SecretListDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());

		SecretListDto responseList = response.getBody();
		assertEquals(2, responseList.getResultList().size());
	}

	@Test
	void testGetValue() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpGet("/value/" + DemoDataProviderService.SECRET_ENTITY2_ID,
				requestEntity, String.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());

		String responseBody = response.getBody();
		assertEquals("test", responseBody);
	}

	@Test
	void testDelete() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpDelete("/" + DemoDataProviderService.SECRET_ENTITY2_ID,
				requestEntity, String.class);

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
		ResponseEntity<String> response = executeHttpPost(
				"/" + DemoDataProviderService.SECRET_ENTITY_ID + "?enabled=" + enabled, requestEntity, String.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());

		SecretEntity entity = secretRepository.getReferenceById(DemoDataProviderService.SECRET_ENTITY_ID);
		assertNotNull(entity);
		assertEquals(enabled ? EntityStatus.ACTIVE : EntityStatus.DISABLED, entity.getStatus());

		executeHttpPost("/" + DemoDataProviderService.SECRET_ENTITY_ID + "?enabled=" + true, requestEntity,
				String.class);
	}

	@Test
	void testCount() {
		HttpEntity<GetSecureValueDto> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		// act
		ResponseEntity<LongValueDto> response = executeHttpGet("/count", requestEntity, LongValueDto.class);

		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(2L, response.getBody().getValue());
	}

	@Test
	void testRotateSecret() {
		String oldValue = secretRepository.findById(DemoDataProviderService.SECRET_ENTITY_ID).get().getValue();
		
		// act
		HttpEntity<SaveSecretRequestDto> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpPost("/rotate/" + DemoDataProviderService.SECRET_ENTITY_ID, requestEntity, String.class);

		// Assert
		String newValue = secretRepository.findById(DemoDataProviderService.SECRET_ENTITY_ID).get().getValue();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotEquals(oldValue, newValue);
	}
}