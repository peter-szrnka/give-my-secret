package io.github.gms.functions.secret;

import io.github.gms.abstraction.AbstractClientControllerIntegrationTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.common.dto.BooleanValueDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.functions.secret.dto.SaveSecretRequestDto;
import io.github.gms.functions.secret.dto.SecretDto;
import io.github.gms.functions.secret.dto.SecretListDto;
import io.github.gms.functions.secret.dto.SecretValueDto;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static io.github.gms.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Tag(TAG_INTEGRATION_TEST)
@TestedClass(SecretController.class)
class SecretIntegrationTest extends AbstractClientControllerIntegrationTest {

	SecretIntegrationTest() {
		super("/secure/secret");
	}
	
	@BeforeAll
	public static void setupAll() throws IOException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		try (InputStream jksFileStream = classloader.getResourceAsStream("test.jks")) {
			byte[] buffer = new byte[jksFileStream.available()];
			jksFileStream.read(buffer);

			new File("./keystores/1/").mkdirs();
			FileCopyUtils.copy(buffer, new File("./keystores/1/test.jks"));
		}
	}

	@AfterAll
	public static void tearDownAll() {
		new File("./keystores/1/test.jks").delete();
		new File("./keystores/1/").delete();
		new File("./keystores").delete();
	}

	@Test
	@TestedMethod(SAVE)
	void save_whenInputIsValid_thenReturnOk() {
		// act
		HttpEntity<SaveSecretRequestDto> requestEntity = new HttpEntity<>(
				TestUtils.createSaveSecretRequestDto(DemoData.SECRET_ENTITY_ID),
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
	@TestedMethod(GET_BY_ID)
	void getById_whenInputIsValid_thenReturnOk() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<SecretDto> response = executeHttpGet("/" + DemoData.SECRET_ENTITY_ID,
				requestEntity, SecretDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());

		SecretDto responseBody = response.getBody();
		assertEquals(DemoData.SECRET_ENTITY_ID, responseBody.getId());
		assertEquals(EntityStatus.ACTIVE, responseBody.getStatus());
	}

	@Test
	@TestedMethod(LIST)
	void list_whenInputIsValid_thenReturnOk() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<SecretListDto> response = executeHttpGet("/list?page=0&size=10&direction=ASC&property=id", requestEntity, SecretListDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());

		SecretListDto responseList = response.getBody();
		assertEquals(1, responseList.getResultList().size());
	}

	@Test
	@TestedMethod(GET_VALUE)
	void getValue_whenInputIsValid_thenReturnOk() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpGet("/value/" + DemoData.SECRET_ENTITY_ID,
				requestEntity, String.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());

		String responseBody = response.getBody();
		assertEquals("test", responseBody);
	}

	@Test
	@TestedMethod(DELETE)
	void delete_whenInputIsValid_thenReturnOk() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpDelete("/" + DemoData.SECRET_ENTITY2_ID,
				requestEntity, String.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Transactional
	@ParameterizedTest
	@TestedMethod(TOGGLE)
	@ValueSource(booleans = { false, true })
	void toggleStatus_whenInputIsValid_thenReturnOk(boolean enabled) {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpPost(
				"/" + DemoData.SECRET_ENTITY_ID + "?enabled=" + enabled, requestEntity, String.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());

		SecretEntity entity = secretRepository.getReferenceById(DemoData.SECRET_ENTITY_ID);
		assertNotNull(entity);
		assertEquals(enabled ? EntityStatus.ACTIVE : EntityStatus.DISABLED, entity.getStatus());

		executeHttpPost("/" + DemoData.SECRET_ENTITY_ID + "?enabled=" + true, requestEntity,
				String.class);
	}

	@Test
	@TestedMethod(ROTATE_SECRET)
	void rotateSecret_whenInputIsValid_thenReturnOk() {
		String oldValue = secretRepository.findById(DemoData.SECRET_ENTITY_ID).get().getValue();
		
		// act
		HttpEntity<SaveSecretRequestDto> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpPost("/rotate/" + DemoData.SECRET_ENTITY_ID, requestEntity, String.class);

		// Assert
		String newValue = secretRepository.findById(DemoData.SECRET_ENTITY_ID).get().getValue();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotEquals(oldValue, newValue);
	}

	@Test
	@TestedMethod("validateValueLength")
	void validateValueLength_whenInputProvided_thenReturnOk() {
		// act
		SecretValueDto secretValueDto = SecretValueDto.builder()
				.keystoreId(DemoData.KEYSTORE_ID)
				.keystoreAliasId(DemoData.KEYSTORE_ALIAS_ID)
				.secretValues(Map.of("value", "1234567890"))
				.build();
		HttpEntity<SecretValueDto> requestEntity = new HttpEntity<>(secretValueDto, TestUtils.getHttpHeaders(jwt));
		ResponseEntity<BooleanValueDto> response = executeHttpPost("/validate_value_length",
				requestEntity, BooleanValueDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().getValue());
	}
}
