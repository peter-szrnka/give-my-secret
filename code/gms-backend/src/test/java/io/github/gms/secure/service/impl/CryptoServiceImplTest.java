package io.github.gms.secure.service.impl;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.exception.GmsException;
import io.github.gms.common.model.KeystorePair;
import io.github.gms.secure.dto.SaveKeystoreRequestDto;
import io.github.gms.secure.entity.SecretEntity;
import io.github.gms.secure.service.KeystoreDataService;
import io.github.gms.util.TestUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test of {@link CryptoServiceImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class CryptoServiceImplTest extends AbstractLoggingUnitTest {

	private KeystoreDataService keystoreDataService;
	private CryptoServiceImpl service;
	
	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		keystoreDataService = mock(KeystoreDataService.class);
		service = new CryptoServiceImpl(keystoreDataService);
		((Logger) LoggerFactory.getLogger(CryptoServiceImpl.class)).addAppender(logAppender);
	}
	
	@Test
	@SneakyThrows
	void shouldNotValidateKeyStoreFile() {
		// arrange
	    MockMultipartFile sampleFile = getMockMultipartFile("wrong key".getBytes());
	    SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
		
		// act & assert
	    TestUtils.assertGmsException(() -> service.validateKeyStoreFile(dto, sampleFile.getBytes()), "java.io.EOFException");
	    assertTrue(logAppender.list.stream().anyMatch(log -> log.getFormattedMessage().contains("Keystore cannot be loaded!")));
	}
	
	@Test
	@SneakyThrows
	void shouldNotFindAlias() {
		// arrange
		MockMultipartFile sampleFile = getMockMultipartFile(getTestFileContent());
	    SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
	    dto.getAliases().get(0).setAlias("aliasfail");
		
		// act & assert
	    TestUtils.assertGmsException(() -> service.validateKeyStoreFile(dto, sampleFile.getBytes()), "The given alias(aliasfail) is not valid!");
	}
	
	@Test
	@SneakyThrows
	void shouldNot2ValidateKeyStoreFile() {
		// arrange
		MockMultipartFile sampleFile = getMockMultipartFile(getTestFileContent());
	    SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
	    dto.getAliases().get(0).setAliasCredential("testfail");
		
		// act & assert
	    TestUtils.assertGmsException(() -> service.validateKeyStoreFile(dto, sampleFile.getBytes()), "java.security.UnrecoverableKeyException: Cannot recover key");
	    TestUtils.assertLogContains(logAppender, "Keystore cannot be loaded!");
	}
	
	@Test
	@SneakyThrows
	void shouldValidateKeyStoreFile() {
		// arrange
		MockMultipartFile sampleFile = getMockMultipartFile(getTestFileContent());
	    SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
		
		// act
		service.validateKeyStoreFile(dto, sampleFile.getBytes());
		
		// assert
		assertFalse(logAppender.list.stream().anyMatch(log -> log.getFormattedMessage().contains("Keystore cannot be loaded!")));
	}
	
	@Test
	@SneakyThrows
	void shouldNotDecrypt() {
		// arrange
		when(keystoreDataService.getKeystoreData(any(SecretEntity.class))).thenThrow(new IllegalArgumentException("Wrong!"));
		
		// act & assert
		TestUtils.assertGmsException(() -> service.decrypt(TestUtils.createSecretEntity()), "java.lang.IllegalArgumentException: Wrong!");
	}
	
	@Test
	@SneakyThrows
	void shouldDecrypt() {
		// arrange
		KeystorePair mockPair = new KeystorePair(TestUtils.createKeystoreAliasEntity(), createKeyStore());
		when(keystoreDataService.getKeystoreData(any(SecretEntity.class))).thenReturn(mockPair);
		
		// act
		SecretEntity entity = TestUtils.createSecretEntity();
		entity.setValue("I+sC4r7asrdGPuPi+mR3O/hJRZ47gVMTigE40tPfkAbo2hfl7V+KxgvoNg7dUv1Bv/JVLVE1GefmHCIk4KteQpilPdNo6lQnE2YldU0+eldGMUNSZnnjW5Qm946dGyHjb3dd9ZY4xXUfKKdgisJUde5CZySDDbarQAg7FbkQkXJc5rtJ8iJOh8R5QX3OnA8J6YuepmZ6kShYDHZi13O3exCEr+PL9r6ctKKDvH/LG1IldAtMTc7dBAGqxD/WCPdZjGXySEBR5M2eOQlcT6VKvhJM1598hbsx5iZ6IIzfdk9IlvhSFabBLkYF3n/7PKlm0buR/9avvcJxvRprCOV1MA");
	    String response = service.decrypt(entity);
	    
	    // assert
	    assertEquals("test", response);
	}
	
	@Test
	@SneakyThrows
	void shouldNotEncrypt() {
		// arrange
		when(keystoreDataService.getKeystoreData(any(SecretEntity.class))).thenThrow(new IllegalArgumentException("Wrong!"));
		
		// act
		SecretEntity entity = TestUtils.createSecretEntity();
	    GmsException exception = assertThrows(GmsException.class, () -> service.encrypt(entity));
	    
	    // assert
	    assertEquals("java.lang.IllegalArgumentException: Wrong!", exception.getMessage());
	}
	
	@Test
	@SneakyThrows
	void shouldEncrypt() {
		// arrange
		KeystorePair mockPair = new KeystorePair(TestUtils.createKeystoreAliasEntity(), createKeyStore());
		when(keystoreDataService.getKeystoreData(any(SecretEntity.class))).thenReturn(mockPair);
		
		// act
		SecretEntity entity = TestUtils.createSecretEntity();
	    service.encrypt(entity);
	    
	    // assert
	    String decrypted = service.decrypt(entity);
	    assertEquals("test", decrypted);
	}

	private static MockMultipartFile getMockMultipartFile(byte[] content) throws IOException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		try (InputStream jksFileStream = classloader.getResourceAsStream("test.jks")) {
			assert jksFileStream != null;
			return new MockMultipartFile(
					"file",
					"test.jks",
					MediaType.APPLICATION_OCTET_STREAM_VALUE,
					content
			);
		}
	}

	private static byte[] getTestFileContent() throws IOException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		try (InputStream jksFileStream = classloader.getResourceAsStream("test.jks")) {
			assert jksFileStream != null;
			return jksFileStream.readAllBytes();
		}
	}

	private static KeyStore createKeyStore() throws Exception {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream jksFileStream = classloader.getResourceAsStream("test.jks");
		
		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		keystore.load(jksFileStream, null);
		return keystore;
	}
}
