package io.github.gms.functions.secret;

import io.github.gms.abstraction.AbstractClientControllerIntegrationTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import lombok.SneakyThrows;
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
import java.io.InputStream;

import static io.github.gms.util.TestConstants.TAG_INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_INTEGRATION_TEST)
@TestedClass(SecretController.class)
class SecretIntegrationTest extends AbstractClientControllerIntegrationTest {

	SecretIntegrationTest() {
		super("/secure/secret");
	}
	
	@BeforeAll
	@SneakyThrows
	public static void setupAll() {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream jksFileStream = classloader.getResourceAsStream("test.jks");
		
		byte[] buffer = new byte[jksFileStream.available()];
		jksFileStream.read(buffer);

		new File("./keystores/1/").mkdirs();
		FileCopyUtils.copy(buffer, new File("./keystores/1/test.jks"));
	}

	@Test
	void testSave() {
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
	void testGetById() {
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
	void testList() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<SecretListDto> response = executeHttpGet("/list?page=0&size=10&direction=ASC&property=id", requestEntity, SecretListDto.class);

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
		ResponseEntity<String> response = executeHttpGet("/value/" + DemoData.SECRET_ENTITY2_ID,
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
		ResponseEntity<String> response = executeHttpDelete("/" + DemoData.SECRET_ENTITY2_ID,
				requestEntity, String.class);

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
	void testRotateSecret() {
		String oldValue = secretRepository.findById(DemoData.SECRET_ENTITY_ID).get().getValue();
		
		// act
		HttpEntity<SaveSecretRequestDto> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpPost("/rotate/" + DemoData.SECRET_ENTITY_ID, requestEntity, String.class);

		// Assert
		String newValue = secretRepository.findById(DemoData.SECRET_ENTITY_ID).get().getValue();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotEquals(oldValue, newValue);
	}
}
