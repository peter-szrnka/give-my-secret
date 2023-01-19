package io.github.gms.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.google.common.io.Files;
import com.google.gson.Gson;

import io.github.gms.abstraction.AbstractClientControllerIntegrationTest;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.KeyStoreValueType;
import io.github.gms.common.filter.SecureHeaderInitializerFilter;
import io.github.gms.common.util.DemoDataProviderService;
import io.github.gms.secure.controller.KeystoreController;
import io.github.gms.secure.dto.GetSecureValueDto;
import io.github.gms.secure.dto.IdNamePairListDto;
import io.github.gms.secure.dto.KeystoreDto;
import io.github.gms.secure.dto.KeystoreListDto;
import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.entity.KeystoreEntity;
import io.github.gms.util.TestConstants;
import io.github.gms.util.TestUtils;
import io.github.gms.util.TestUtils.ValueHolder;
import lombok.SneakyThrows;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TestConstants.TAG_INTEGRATION_TEST)
class KeystoreIntegrationTest extends AbstractClientControllerIntegrationTest {
	
	@Autowired
	private SecureHeaderInitializerFilter secureHeaderInitializerFilter;
	
	@Autowired
	private Gson gson;
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	KeystoreIntegrationTest() {
		super("/secure/keystore");
	}

	@Test
	@SneakyThrows
	void testSave() {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream jksFileStream = classloader.getResourceAsStream("test.jks");
		
	    MockMultipartFile sampleFile = new MockMultipartFile(
	      KeystoreController.MULTIPART_FILE,
	      "test-" + UUID.randomUUID().toString() + ".jks", 
	      MediaType.APPLICATION_OCTET_STREAM_VALUE,
	      jksFileStream.readAllBytes()
	    );
	    
	    String saveRequestJson = gson.toJson(TestUtils.createSaveKeystoreRequestDto());
	 
	    MockMultipartHttpServletRequestBuilder multipartRequest =
	      MockMvcRequestBuilders.multipart("/secure/keystore")
	      .file(sampleFile);
	    
	    multipartRequest.flashAttr("model", saveRequestJson);
	    
	    multipartRequest.headers(TestUtils.getHttpHeaders(null));
	    multipartRequest.cookie(TestUtils.getCookie(jwt));

	    MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
	    		.addFilter(secureHeaderInitializerFilter, "/secure/keystore")
	    		.build();
	 
	    // act
	    mvc.perform(multipartRequest).andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	void testGetById() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<KeystoreDto> response = executeHttpGet("/" + DemoDataProviderService.KEYSTORE_ID, requestEntity, KeystoreDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		KeystoreDto responseBody = response.getBody();
		assertEquals(DemoDataProviderService.KEYSTORE_ID, responseBody.getId());
		assertEquals(EntityStatus.ACTIVE, responseBody.getStatus());
	}
	
	@Test
	void testList() {
		// act
		PagingDto request = PagingDto.builder().page(0).size(50).direction("ASC").property("id").build();

		HttpEntity<PagingDto> requestEntity = new HttpEntity<>(request, TestUtils.getHttpHeaders(jwt));
		ResponseEntity<KeystoreListDto> response = executeHttpPost("/list", requestEntity, KeystoreListDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		KeystoreListDto responseList = response.getBody();
		assertEquals(2, responseList.getResultList().size());
	}
	
	@ParameterizedTest
	@MethodSource("valueData")
	void testGetSecretValue(ValueHolder input) {
		// act
		GetSecureValueDto dto = new GetSecureValueDto(DemoDataProviderService.KEYSTORE_ID, input.getAliasId(), input.getValueType());
		HttpEntity<GetSecureValueDto> requestEntity = new HttpEntity<>(dto, TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpPost("/value", requestEntity, String.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		String responseBody = response.getBody();
		assertEquals(input.getExpectedValue(), responseBody);
	}
	
	@Test
	@SneakyThrows
	void testDelete() {
		// arrange
		KeystoreEntity newEntity = keystoreRepository.save(TestUtils.createNewKeystoreEntity(3L));
		
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpDelete("/" + newEntity.getId(), requestEntity,
				String.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
		
		Files.copy(new File("src/test/resources/test.jks"), new File("src/test/resources/1/test.jks"));
	}
	
	@Transactional
	@ParameterizedTest
	@ValueSource(booleans = { false, true })
	void testToggleStatus(boolean enabled) {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpPost("/" + DemoDataProviderService.KEYSTORE_ID + "?enabled="+ enabled, requestEntity,
				String.class);

		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
		
		KeystoreEntity entity = keystoreRepository.getReferenceById(DemoDataProviderService.KEYSTORE_ID);
		assertNotNull(entity);
		assertEquals(enabled ? EntityStatus.ACTIVE : EntityStatus.DISABLED, entity.getStatus());
		
		executeHttpPost("/" + DemoDataProviderService.KEYSTORE_ID + "?enabled="+ true, requestEntity,String.class);
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
	void testGetAllKeystoreNames() {
		HttpEntity<GetSecureValueDto> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		
		// act
		ResponseEntity<IdNamePairListDto> response = executeHttpGet("/list_names", requestEntity, IdNamePairListDto.class);
		
		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertFalse(response.getBody().getResultList().isEmpty());
		assertEquals(2, response.getBody().getResultList().size());
	}
	
	@Test
	void testGetAllKeystoreAliasNames() {
		HttpEntity<GetSecureValueDto> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		
		// act
		ResponseEntity<IdNamePairListDto> response = executeHttpGet("/list_aliases/" + DemoDataProviderService.KEYSTORE_ID, requestEntity, IdNamePairListDto.class);
		
		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertFalse(response.getBody().getResultList().isEmpty());
		assertEquals(2, response.getBody().getResultList().size());
	}
	
	public static List<ValueHolder> valueData() {
		return Lists.newArrayList(
				new ValueHolder(KeyStoreValueType.KEYSTORE_ALIAS, DemoDataProviderService.KEYSTORE_ALIAS_ID, "test"),
				new ValueHolder(KeyStoreValueType.KEYSTORE_ALIAS_CREDENTIAL, DemoDataProviderService.KEYSTORE_ALIAS_ID, "test"),
				new ValueHolder(KeyStoreValueType.KEYSTORE_CREDENTIAL, null, "test")
		);
	}
}