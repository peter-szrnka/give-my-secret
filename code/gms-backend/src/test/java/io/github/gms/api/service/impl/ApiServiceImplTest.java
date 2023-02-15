package io.github.gms.api.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.SecretType;
import io.github.gms.common.exception.GmsException;
import io.github.gms.secure.dto.ApiResponseDto;
import io.github.gms.secure.dto.CredentialPairApiResponseDto;
import io.github.gms.secure.dto.GetSecretRequestDto;
import io.github.gms.secure.dto.SimpleApiResponseDto;
import io.github.gms.secure.entity.ApiKeyEntity;
import io.github.gms.secure.entity.SecretEntity;
import io.github.gms.secure.entity.UserEntity;
import io.github.gms.secure.repository.ApiKeyRepository;
import io.github.gms.secure.repository.ApiKeyRestrictionRepository;
import io.github.gms.secure.repository.KeystoreAliasRepository;
import io.github.gms.secure.repository.KeystoreRepository;
import io.github.gms.secure.repository.SecretRepository;
import io.github.gms.secure.repository.UserRepository;
import io.github.gms.secure.service.CryptoService;
import io.github.gms.util.TestUtils;
import lombok.SneakyThrows;

/**
 * Unit test of {@link ApiServiceImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class ApiServiceImplTest extends AbstractUnitTest {

	private static GetSecretRequestDto dto = new GetSecretRequestDto("12345678", "123456");

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private CryptoService cryptoService;

	@Mock
	private SecretRepository secretRepository;

	@Mock
	private ApiKeyRepository apiKeyRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private KeystoreRepository keystoreRepository;

	@Mock
	private KeystoreAliasRepository keystoreAliasRepository;

	@Mock
	private ApiKeyRestrictionRepository apiKeyRestrictionRepository;

	@InjectMocks
	private ApiServiceImpl service;

	@Test
	void shouldApiKeyMissing() {
		// arrange
		when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(null);

		// assert
		GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.getSecret(dto));
		assertEquals("Wrong API key!", exception.getMessage());
		
		verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
		verify(userRepository, never()).findById(anyLong());
	}

	@Test
	void shouldUserMissing() {
		// arrange
		when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());

		// assert
		GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.getSecret(dto));
		assertEquals("User not found!", exception.getMessage());
		
		verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
		verify(userRepository).findById(anyLong());
	}

	@Test
	void shouldSecretMissing() {
		// arrange
		when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
		when(userRepository.findById(anyLong())).thenReturn(createMockUser());
		when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn(null);

		// assert
		GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.getSecret(dto));
		assertEquals("Secret is not available!", exception.getMessage());
		
		verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
		verify(userRepository).findById(anyLong());
		verify(secretRepository).findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE));
	}

	@Test
	void shouldKeystoreAliasMissing() {
		// arrange
		when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
		when(userRepository.findById(anyLong())).thenReturn(createMockUser());
		when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn(createMockSecret("encrypted", false, SecretType.CREDENTIAL));
		when(keystoreAliasRepository.findById(anyLong())).thenReturn(Optional.empty());

		// assert
		GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.getSecret(dto));
		assertEquals("Keystore alias is not available!", exception.getMessage());
		
		verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
		verify(userRepository).findById(anyLong());
		verify(secretRepository).findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE));
		verify(keystoreAliasRepository).findById(anyLong());
	}

	@Test
	void shouldKeystoreMissing() {
		// arrange
		when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
		when(userRepository.findById(anyLong())).thenReturn(createMockUser());
		when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn(createMockSecret("encrypted", false, SecretType.CREDENTIAL));
		when(keystoreAliasRepository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));
		when(keystoreRepository.findByIdAndUserIdAndStatus(anyLong(), anyLong(), eq(EntityStatus.ACTIVE))).thenReturn(Optional.empty());

		// assert
		GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.getSecret(dto));
		assertEquals("Invalid keystore!", exception.getMessage());
		
		verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
		verify(userRepository).findById(anyLong());
		verify(secretRepository).findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE));
		verify(keystoreAliasRepository).findById(anyLong());
		verify(keystoreRepository).findByIdAndUserIdAndStatus(anyLong(), anyLong(), eq(EntityStatus.ACTIVE));
	}

	@Test
	void shouldFailBecauseOfApiKeyRestriction() {
		// arrange
		when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
		when(userRepository.findById(anyLong())).thenReturn(createMockUser());
		when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn(createMockSecret("encrypted", false, SecretType.CREDENTIAL));
		when(apiKeyRestrictionRepository.findAllByUserIdAndSecretId(anyLong(), anyLong())).thenReturn(List.of(
				TestUtils.createApiKeyRestrictionEntity(2L),
				TestUtils.createApiKeyRestrictionEntity(3L)
				));

		// assert
		GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.getSecret(dto));
		assertEquals("You are not allowed to use this API key for this secret!", exception.getMessage());
		
		verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
		verify(userRepository).findById(anyLong());
		verify(secretRepository).findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE));
		verify(apiKeyRestrictionRepository).findAllByUserIdAndSecretId(anyLong(), anyLong());
	}

	@Test
	@SneakyThrows
	void shouldFailWhenCredentilPairIsInvalid() {
		// arrange
		when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
		when(userRepository.findById(anyLong())).thenReturn(createMockUser());
				
		String mockValue = "{\"username\":\"u\",\"password\":\"p\"}";
		when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn(createMockSecret(mockValue, true, SecretType.CREDENTIAL_PAIR));
		when(keystoreRepository.findByIdAndUserIdAndStatus(anyLong(), anyLong(), eq(EntityStatus.ACTIVE))).thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		when(keystoreAliasRepository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));
		when(apiKeyRestrictionRepository.findAllByUserIdAndSecretId(anyLong(), anyLong())).thenReturn(List.of(
				TestUtils.createApiKeyRestrictionEntity(1L),
				TestUtils.createApiKeyRestrictionEntity(2L),
				TestUtils.createApiKeyRestrictionEntity(3L)
				));
				
		when(cryptoService.decrypt(any(SecretEntity.class))).thenReturn("{\"username\":\"u\",\"password\":\"p\"}");
		when(objectMapper.readValue(anyString(), eq(CredentialPairApiResponseDto.class))).thenThrow(JsonProcessingException.class);

		// act & assert
		GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.getSecret(dto));
		assertEquals("com.fasterxml.jackson.core.JsonProcessingException: N/A", exception.getMessage());
	}

	@SneakyThrows
	@ParameterizedTest
	@MethodSource("inputData")
	@SuppressWarnings("unchecked")
	<T extends ApiResponseDto> void shouldReturnEncrypted(boolean returnDecrypted, Class<T> expectedResponseType, SecretType type) {
		// arrange
		when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
		when(userRepository.findById(anyLong())).thenReturn(createMockUser());
		
		String mockValue = "encrypted";
		if (expectedResponseType == CredentialPairApiResponseDto.class) {
			mockValue = "{\"username\":\"u\",\"password\":\"p\"}";
		}
		when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn(createMockSecret(mockValue, returnDecrypted, type));
		when(keystoreRepository.findByIdAndUserIdAndStatus(anyLong(), anyLong(), eq(EntityStatus.ACTIVE))).thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		when(keystoreAliasRepository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));
		when(apiKeyRestrictionRepository.findAllByUserIdAndSecretId(anyLong(), anyLong())).thenReturn(List.of(
				TestUtils.createApiKeyRestrictionEntity(1L),
				TestUtils.createApiKeyRestrictionEntity(2L),
				TestUtils.createApiKeyRestrictionEntity(3L)
				));
		
		if (returnDecrypted) {
			if (expectedResponseType == CredentialPairApiResponseDto.class) {
				when(cryptoService.decrypt(any(SecretEntity.class))).thenReturn("{\"username\":\"u\",\"password\":\"p\"}");
			} else {
				when(cryptoService.decrypt(any(SecretEntity.class))).thenReturn("decrypted");
			}
		}
		
		if (expectedResponseType == CredentialPairApiResponseDto.class) {
			when(objectMapper.readValue(anyString(), eq(CredentialPairApiResponseDto.class))).thenReturn(new CredentialPairApiResponseDto("u", "p"));
		}

		// act
		T response = (T) service.getSecret(dto);
		
		// assert
		Assertions.assertNotNull(response);
		if (expectedResponseType == SimpleApiResponseDto.class) {
			Assertions.assertEquals(returnDecrypted ? "decrypted" : "encrypted", ((SimpleApiResponseDto) response).getValue());
		} else {
			Assertions.assertEquals("u", ((CredentialPairApiResponseDto) response).getUsername());
		}
		verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
		verify(userRepository).findById(anyLong());
		verify(secretRepository).findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE));
		verify(cryptoService, returnDecrypted ? times(1) : never()).decrypt(any(SecretEntity.class));
		verify(keystoreAliasRepository).findById(anyLong());
		verify(keystoreRepository).findByIdAndUserIdAndStatus(anyLong(), anyLong(), eq(EntityStatus.ACTIVE));
	}

	public static Object[][] inputData() {
		return new Object[][] { { true, SimpleApiResponseDto.class, SecretType.CREDENTIAL },
				{ false, SimpleApiResponseDto.class, SecretType.CREDENTIAL },
				{ true, CredentialPairApiResponseDto.class, SecretType.CREDENTIAL_PAIR },
				{ false, SimpleApiResponseDto.class, SecretType.CREDENTIAL_PAIR } };
	}

	private static SecretEntity createMockSecret(String value, boolean returnDecrypted, SecretType type) {
		SecretEntity entity = new SecretEntity();
		entity.setId(1L);
		entity.setValue(value);
		entity.setReturnDecrypted(returnDecrypted);
		entity.setKeystoreAliasId(1L);
		entity.setUserId(1L);
		entity.setKeystoreAliasId(1L);
		entity.setType(type);
		return entity;
	}

	private static Optional<UserEntity> createMockUser() {
		UserEntity entity = new UserEntity();
		return Optional.of(entity);
	}

	private static ApiKeyEntity createApiKeyEntity() {
		ApiKeyEntity mockApiKey = new ApiKeyEntity();
		mockApiKey.setId(1L);
		mockApiKey.setUserId(1L);
		mockApiKey.setValue("apikey");

		return mockApiKey;
	}
}
