package io.github.gms.api.service.impl;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.SecretType;
import io.github.gms.common.exception.GmsException;
import io.github.gms.secure.dto.GetSecretRequestDto;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.github.gms.common.util.Constants.VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class ApiServiceImplTest extends AbstractUnitTest {

	private static final GetSecretRequestDto dto = new GetSecretRequestDto("12345678", "123456");

	private CryptoService cryptoService;
	private SecretRepository secretRepository;
	private ApiKeyRepository apiKeyRepository;
	private UserRepository userRepository;
	private KeystoreRepository keystoreRepository;
	private KeystoreAliasRepository keystoreAliasRepository;
	private ApiKeyRestrictionRepository apiKeyRestrictionRepository;
	private ApiServiceImpl service;

	@BeforeEach
	void beforeEach() {
		cryptoService = mock(CryptoService.class);
		secretRepository = mock(SecretRepository.class);
		apiKeyRepository = mock(ApiKeyRepository.class);
		userRepository = mock(UserRepository.class);
		keystoreRepository = mock(KeystoreRepository.class);
		keystoreAliasRepository = mock(KeystoreAliasRepository.class);
		apiKeyRestrictionRepository = mock(ApiKeyRestrictionRepository.class);
		service = new ApiServiceImpl(cryptoService, secretRepository, apiKeyRepository, userRepository, keystoreRepository,
				keystoreAliasRepository, apiKeyRestrictionRepository);
	}

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
		when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn(Optional.empty());

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
		when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn(Optional.of(createMockSecret("encrypted", false, SecretType.SIMPLE_CREDENTIAL)));
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
		when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn((Optional.of(createMockSecret("encrypted", false, SecretType.SIMPLE_CREDENTIAL))));
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
		when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn((Optional.of(createMockSecret("encrypted", false, SecretType.SIMPLE_CREDENTIAL))));
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

	@SneakyThrows
	@ParameterizedTest
	@MethodSource("inputData")
	void shouldReturnValue(boolean returnDecrypted, SecretType type, String expectedValue) {
		// arrange
		when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
		when(userRepository.findById(anyLong())).thenReturn(createMockUser());

		when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn((Optional.of(createMockSecret(expectedValue, returnDecrypted, type))));
		when(keystoreRepository.findByIdAndUserIdAndStatus(anyLong(), anyLong(), eq(EntityStatus.ACTIVE))).thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		when(keystoreAliasRepository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));
		when(apiKeyRestrictionRepository.findAllByUserIdAndSecretId(anyLong(), anyLong())).thenReturn(List.of(
				TestUtils.createApiKeyRestrictionEntity(1L),
				TestUtils.createApiKeyRestrictionEntity(2L),
				TestUtils.createApiKeyRestrictionEntity(3L)
		));

		if (returnDecrypted) {
			when(cryptoService.decrypt(any(SecretEntity.class))).thenReturn(expectedValue);
		}

		// act
		Map<String, String> response = service.getSecret(dto);

		// assert
		Assertions.assertNotNull(response);

		if (type == SecretType.SIMPLE_CREDENTIAL) {
			assertEquals(expectedValue, response.get(VALUE));
		} else if (returnDecrypted) {
			assertEquals("u", response.get("username"));
			assertEquals("p", response.get("password"));
		} else {
			assertEquals("encrypted", response.get(VALUE));
		}

		verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
		verify(userRepository).findById(anyLong());
		verify(secretRepository).findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE));
		verify(cryptoService, returnDecrypted ? times(1) : never()).decrypt(any(SecretEntity.class));
		verify(keystoreAliasRepository).findById(anyLong());
		verify(keystoreRepository).findByIdAndUserIdAndStatus(anyLong(), anyLong(), eq(EntityStatus.ACTIVE));

		if (returnDecrypted) {
			verify(cryptoService).decrypt(any(SecretEntity.class));
		}
	}

	public static Object[][] inputData() {
		return new Object[][] { { true, SecretType.SIMPLE_CREDENTIAL, "decrypted" },
				{ false, SecretType.SIMPLE_CREDENTIAL, "encrypted" },
				{ true, SecretType.MULTIPLE_CREDENTIAL, "username:u;password:p" },
				{ false, SecretType.MULTIPLE_CREDENTIAL, "encrypted" } };
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
