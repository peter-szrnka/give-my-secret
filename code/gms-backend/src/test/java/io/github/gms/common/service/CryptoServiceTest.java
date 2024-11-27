package io.github.gms.common.service;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.model.KeystorePair;
import io.github.gms.common.types.GmsException;
import io.github.gms.functions.keystore.KeystoreDataService;
import io.github.gms.functions.keystore.SaveKeystoreRequestDto;
import io.github.gms.functions.secret.SecretEntity;
import io.github.gms.util.TestUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import static io.github.gms.util.LogAssertionUtils.assertLogContains;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class CryptoServiceTest extends AbstractLoggingUnitTest {

	private KeystoreDataService keystoreDataService;
	private CryptoService service;
	
	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		keystoreDataService = mock(KeystoreDataService.class);
		service = new CryptoService(keystoreDataService);
		addAppender(CryptoService.class);
	}
	
	@Test
	@SneakyThrows
	void validateKeyStoreFile_whenKeyIsInvalid_thenThrowGmsException() {
		// arrange
	    MockMultipartFile sampleFile = getMockMultipartFile("wrong key".getBytes());
	    SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
		
		// act & assert
	    TestUtils.assertGmsException(() -> service.validateKeyStoreFile(dto, sampleFile.getBytes()), "java.io.EOFException");
		assertLogContains(logAppender, "Keystore cannot be loaded!");
	}
	
	@Test
	@SneakyThrows
	void validateKeyStoreFile_whenAliasIsInvalid_thenThrowGmsException() {
		// arrange
		MockMultipartFile sampleFile = getMockMultipartFile(getTestFileContent());
	    SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
	    dto.getAliases().getFirst().setAlias("aliasfail");
		
		// act & assert
	    TestUtils.assertGmsException(() -> service.validateKeyStoreFile(dto, sampleFile.getBytes()), "The given alias(aliasfail) is not valid!");
	}
	
	@Test
	@SneakyThrows
	void validateKeyStoreFile_whenAliasCredentialIsInvalid_thenThrowGmsException() {
		// arrange
		MockMultipartFile sampleFile = getMockMultipartFile(getTestFileContent());
	    SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
	    dto.getAliases().getFirst().setAliasCredential("testfail");
		
		// act & assert
	    TestUtils.assertGmsException(() -> service.validateKeyStoreFile(dto, sampleFile.getBytes()), "java.security.UnrecoverableKeyException: Cannot recover key");
	    assertLogContains(logAppender, "Keystore cannot be loaded!");
	}
	
	@Test
	@SneakyThrows
	void validateKeyStoreFile_whenDataIsValid_thenLogMessage() {
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
	void service_whenDataIsInvalid_thenThrowGmsException() {
		// arrange
		when(keystoreDataService.getKeystoreData(any(SecretEntity.class))).thenThrow(new IllegalArgumentException("Wrong!"));
		
		// act & assert
		TestUtils.assertGmsException(() -> service.decrypt(TestUtils.createSecretEntity()), "java.lang.IllegalArgumentException: Wrong!");
	}
	
	@Test
	@SneakyThrows
	void service_whenDataIsDecryptable_thenDecrypt() {
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
	void encrypt_whenKeystoreEntityIsInvalid_thenThrowIllegalArgumentException() {
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
	void encrypt_whenEntityIsValid_thenEncryptData() {
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
