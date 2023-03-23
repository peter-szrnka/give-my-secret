package io.github.gms.secure.service.impl;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Sets;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.SecretType;
import io.github.gms.common.exception.GmsException;
import io.github.gms.common.util.MdcUtils;
import io.github.gms.secure.converter.SecretConverter;
import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.dto.SaveSecretRequestDto;
import io.github.gms.secure.dto.SecretDto;
import io.github.gms.secure.dto.SecretListDto;
import io.github.gms.secure.entity.ApiKeyRestrictionEntity;
import io.github.gms.secure.entity.KeystoreEntity;
import io.github.gms.secure.entity.SecretEntity;
import io.github.gms.secure.repository.ApiKeyRestrictionRepository;
import io.github.gms.secure.repository.KeystoreAliasRepository;
import io.github.gms.secure.repository.KeystoreRepository;
import io.github.gms.secure.repository.SecretRepository;
import io.github.gms.secure.service.CryptoService;
import io.github.gms.util.TestUtils;
import lombok.SneakyThrows;
import org.assertj.core.util.Lists;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class SecretServiceImplTest extends AbstractLoggingUnitTest {

	private CryptoService cryptoService;
	private KeystoreRepository keystoreRepository;
	private KeystoreAliasRepository keystoreAliasRepository;
	private SecretRepository repository;
	private SecretConverter converter;
	private ApiKeyRestrictionRepository apiKeyRestrictionRepository;
	private SecretServiceImpl service;

	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		cryptoService = mock(CryptoService.class);
		keystoreRepository = mock(KeystoreRepository.class);
		keystoreAliasRepository = mock(KeystoreAliasRepository.class);
		repository = mock(SecretRepository.class);
		converter = mock(SecretConverter.class);
		apiKeyRestrictionRepository = mock(ApiKeyRestrictionRepository.class);
		service = new SecretServiceImpl(cryptoService, keystoreRepository, keystoreAliasRepository, repository,
				converter, apiKeyRestrictionRepository);
		((Logger) LoggerFactory.getLogger(SecretServiceImpl.class)).addAppender(logAppender);

		MDC.put(MdcParameter.USER_ID.getDisplayName(), 1L);
	}

	@Test
	void shouldNotSaveNewEntityWhenKeystoreNotSent() {
		// arrange
		SaveSecretRequestDto dto = TestUtils.createSaveSecretRequestDto(2L);
		dto.setKeystoreAliasId(null);

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.save(dto));

		// assert
		assertEquals(SecretServiceImpl.WRONG_KEYSTORE_ALIAS, exception.getMessage());
		verify(keystoreRepository, never()).findByIdAndUserId(anyLong(), anyLong());
	}

	@Test
	void shouldNotSaveNewEntityWhenKeystoreAliasDoesNotExists() {
		// arrange
		when(keystoreAliasRepository.findById(anyLong())).thenReturn(Optional.empty());

		// act & assert
		SaveSecretRequestDto input = TestUtils.createSaveSecretRequestDto(2L);
		TestUtils.assertGmsException(() -> service.save(input), SecretServiceImpl.WRONG_KEYSTORE_ALIAS);
		verify(keystoreAliasRepository).findById(anyLong());
	}

	@Test
	void shouldNotSaveNewEntityWhenKeystoreDoesNotExists() {
		// arrange
		when(keystoreRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
		when(keystoreAliasRepository.findById(anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));

		// act & assert
		SaveSecretRequestDto input = TestUtils.createSaveSecretRequestDto(2L);
		TestUtils.assertGmsException(() -> service.save(input), SecretServiceImpl.WRONG_ENTITY);
		verify(keystoreRepository).findByIdAndUserId(anyLong(), anyLong());
		verify(keystoreAliasRepository).findById(anyLong());
	}

	@Test
	void shouldNotSaveNewEntityWhenKeystoreDisabled() {
		// arrange
		KeystoreEntity mockKeystoreEntity = TestUtils.createKeystoreEntity();
		mockKeystoreEntity.setStatus(EntityStatus.DISABLED);
		when(keystoreRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(mockKeystoreEntity));
		when(keystoreAliasRepository.findById(anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));

		// act & assert
		SaveSecretRequestDto input = TestUtils.createSaveSecretRequestDto(2L);
		TestUtils.assertGmsException(() -> service.save(input), SecretServiceImpl.PLEASE_PROVIDE_ACTIVE_KEYSTORE);
		verify(keystoreRepository).findByIdAndUserId(anyLong(), anyLong());
		verify(keystoreAliasRepository).findById(anyLong());
	}

	@Test
	void shouldNotSaveNewEntityWhenKeystoreIsNotRelatedToAlias() {
		// arrange
		KeystoreEntity mockKeystoreEntity = TestUtils.createKeystoreEntity();
		when(keystoreRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(mockKeystoreEntity));
		when(keystoreAliasRepository.findById(anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));

		// act & assert
		SaveSecretRequestDto input = TestUtils.createSaveSecretRequestDto(2L);
		input.setKeystoreId(9L);
		TestUtils.assertGmsException(() -> service.save(input), "Invalid keystore defined in the request!");
		verify(keystoreRepository).findByIdAndUserId(anyLong(), anyLong());
		verify(keystoreAliasRepository).findById(anyLong());
	}

	@Test
	void shouldNotSaveNewEntityWhenSecretIdIsNotUnique() {
		// arrange
		MockedStatic<MdcUtils> mockedMdcUtils = mockStatic(MdcUtils.class);
		mockedMdcUtils.when(MdcUtils::getUserId).thenReturn(1L);
		SecretEntity mockEntity = TestUtils.createSecretEntity();
		when(keystoreRepository.findByIdAndUserId(anyLong(), anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		when(keystoreAliasRepository.findById(anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));
		when(repository.countAllSecretsByUserIdAndSecretId(anyLong(), anyString())).thenReturn(1L);

		// act & assert
		SaveSecretRequestDto input = TestUtils.createNewSaveSecretRequestDto();
		TestUtils.assertGmsException(() -> service.save(input), "Secret ID name must be unique!");

		verify(keystoreRepository).findByIdAndUserId(anyLong(), anyLong());
		verify(converter, never()).toNewEntity(any(SaveSecretRequestDto.class));
		verify(cryptoService, never()).encrypt(mockEntity);
		verify(keystoreAliasRepository).findById(anyLong());
		verify(repository, never()).save(any(SecretEntity.class));

		mockedMdcUtils.close();
	}

	@ParameterizedTest
	@ValueSource(strings = { "a", "a;", "a;b", "a:x;b:" })
	void shouldNotSaveNewEntityWhenUsernamePasswordPairisInvalid(String inputValue) {
		// arrange
		MockedStatic<MdcUtils> mockedMdcUtils = mockStatic(MdcUtils.class);
		mockedMdcUtils.when(MdcUtils::getUserId).thenReturn(1L);
		SecretEntity mockEntity = TestUtils.createSecretEntity();
		when(keystoreRepository.findByIdAndUserId(anyLong(), anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		when(keystoreAliasRepository.findById(anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));
		when(repository.countAllSecretsByUserIdAndSecretId(anyLong(), anyString())).thenReturn(0L);

		// act & assert
		SaveSecretRequestDto input = TestUtils.createNewSaveSecretRequestDto();
		input.setType(SecretType.MULTIPLE_CREDENTIAL);
		input.setValue(inputValue);
		TestUtils.assertGmsException(() -> service.save(input), "Username password pair is invalid!");

		verify(keystoreRepository).findByIdAndUserId(anyLong(), anyLong());
		verify(converter, never()).toNewEntity(any(SaveSecretRequestDto.class));
		verify(cryptoService, never()).encrypt(mockEntity);
		verify(keystoreAliasRepository).findById(anyLong());
		verify(repository, never()).save(any(SecretEntity.class));

		mockedMdcUtils.close();
	}

	@Test
	@SneakyThrows
	void shouldNotSaveNewEntityWhenUsernamePasswordPairIsInvalid2() {
		// arrange
		MockedStatic<MdcUtils> mockedMdcUtils = mockStatic(MdcUtils.class);
		mockedMdcUtils.when(MdcUtils::getUserId).thenReturn(1L);
		SecretEntity mockEntity = TestUtils.createSecretEntity();
		when(keystoreRepository.findByIdAndUserId(anyLong(), anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		when(keystoreAliasRepository.findById(anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));
		when(repository.countAllSecretsByUserIdAndSecretId(anyLong(), anyString())).thenReturn(0L);

		// act & assert
		SaveSecretRequestDto input = TestUtils.createNewSaveSecretRequestDto();
		input.setType(SecretType.MULTIPLE_CREDENTIAL);
		input.setValue("");
		TestUtils.assertGmsException(() -> service.save(input), "Username password pair is invalid!");

		verify(keystoreRepository).findByIdAndUserId(anyLong(), anyLong());
		verify(converter, never()).toNewEntity(any(SaveSecretRequestDto.class));
		verify(cryptoService, never()).encrypt(mockEntity);
		verify(keystoreAliasRepository).findById(anyLong());
		verify(repository, never()).save(any(SecretEntity.class));

		mockedMdcUtils.close();
	}

	@Test
	@SneakyThrows
	void shouldSaveNewEntityWhenUsernamePasswordPairIsValid() {
		// arrange
		MockedStatic<MdcUtils> mockedMdcUtils = mockStatic(MdcUtils.class);
		mockedMdcUtils.when(MdcUtils::getUserId).thenReturn(1L);
		SecretEntity mockEntity = TestUtils.createSecretEntity();
		when(keystoreRepository.findByIdAndUserId(anyLong(), anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		when(keystoreAliasRepository.findById(anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));
		when(converter.toNewEntity(any(SaveSecretRequestDto.class))).thenReturn(mockEntity);
		when(repository.countAllSecretsByUserIdAndSecretId(anyLong(), anyString())).thenReturn(0L);
		when(apiKeyRestrictionRepository.findAllByUserIdAndSecretId(anyLong(), anyLong())).thenReturn(List.of());
		when(repository.save(any(SecretEntity.class))).thenReturn(mockEntity);

		// act
		SaveSecretRequestDto input = TestUtils.createNewSaveSecretRequestDto();
		input.setType(SecretType.MULTIPLE_CREDENTIAL);
		input.setValue("username:test;password:y");

		SaveEntityResponseDto response = service.save(input);

		// assert
		assertNotNull(response);
		assertEquals(1L, response.getEntityId());
		verify(keystoreRepository).findByIdAndUserId(anyLong(), anyLong());
		verify(converter).toNewEntity(any(SaveSecretRequestDto.class));
		verify(cryptoService).encrypt(mockEntity);
		verify(keystoreAliasRepository).findById(anyLong());
		verify(repository).save(any(SecretEntity.class));

		mockedMdcUtils.close();
	}

	@Test
	void shouldSaveNewEntity() {
		// arrange
		SecretEntity mockEntity = TestUtils.createSecretEntity();
		when(keystoreRepository.findByIdAndUserId(anyLong(), anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		when(converter.toNewEntity(any(SaveSecretRequestDto.class))).thenReturn(mockEntity);
		when(repository.save(any(SecretEntity.class))).thenReturn(mockEntity);
		when(keystoreAliasRepository.findById(anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));

		// act
		SaveEntityResponseDto response = service.save(TestUtils.createNewSaveSecretRequestDto());

		// assert
		assertNotNull(response);
		assertEquals(1L, response.getEntityId());
		verify(keystoreRepository).findByIdAndUserId(anyLong(), anyLong());
		verify(converter).toNewEntity(any(SaveSecretRequestDto.class));
		verify(cryptoService).encrypt(mockEntity);
		verify(keystoreAliasRepository).findById(anyLong());
	}

	@Test
	void shouldSaveNewEntityWithApiKeyRestrictions() {
		// arrange
		SecretEntity mockEntity = TestUtils.createSecretEntity();
		when(keystoreRepository.findByIdAndUserId(anyLong(), anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		when(converter.toNewEntity(any(SaveSecretRequestDto.class))).thenReturn(mockEntity);
		when(repository.save(any(SecretEntity.class))).thenReturn(mockEntity);
		when(keystoreAliasRepository.findById(anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));

		// act
		Set<Long> apiKeys = Sets.newHashSet(3L, 4L, 5L);
		SaveEntityResponseDto response = service.save(TestUtils.createSaveSecretRequestDto(null, apiKeys));

		// assert
		assertNotNull(response);
		assertEquals(1L, response.getEntityId());
		verify(keystoreRepository).findByIdAndUserId(anyLong(), anyLong());
		verify(converter).toNewEntity(any(SaveSecretRequestDto.class));
		verify(cryptoService).encrypt(mockEntity);
		verify(apiKeyRestrictionRepository, times(3)).save(any(ApiKeyRestrictionEntity.class));
		verify(keystoreAliasRepository).findById(anyLong());
	}

	@Test
	void shouldNotSaveExistingEntity() {
		// arrange
		SecretEntity mockEntity = TestUtils.createSecretEntity();
		when(keystoreRepository.findByIdAndUserId(anyLong(), anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		when(keystoreAliasRepository.findById(anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));

		// act & assert
		TestUtils.assertGmsException(() -> service.save(TestUtils.createSaveSecretRequestDto(2L)), "Secret not found!");

		verify(keystoreRepository).findByIdAndUserId(anyLong(), anyLong());
		verify(converter, never()).toEntity(any(SecretEntity.class), any(SaveSecretRequestDto.class));
		verify(cryptoService, never()).encrypt(mockEntity);
		verify(keystoreAliasRepository).findById(anyLong());

		MDC.clear();
	}

	@Test
	void shouldSaveExistingEntity() {
		// arrange
		SecretEntity mockEntity = TestUtils.createSecretEntity();
		when(keystoreRepository.findByIdAndUserId(anyLong(), anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		when(repository.findById(1L)).thenReturn(Optional.of(mockEntity));
		when(converter.toEntity(any(SecretEntity.class), any(SaveSecretRequestDto.class))).thenReturn(mockEntity);
		when(repository.save(any(SecretEntity.class))).thenReturn(mockEntity);
		when(keystoreAliasRepository.findById(anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));

		// act
		SaveEntityResponseDto response = service.save(TestUtils.createSaveSecretRequestDto(1L));

		// assert
		assertNotNull(response);
		assertEquals(1L, response.getEntityId());
		verify(keystoreRepository).findByIdAndUserId(anyLong(), anyLong());
		verify(repository).findById(1L);
		verify(converter).toEntity(any(SecretEntity.class), any(SaveSecretRequestDto.class));
		verify(cryptoService).encrypt(mockEntity);
		verify(keystoreAliasRepository).findById(anyLong());
	}

	@Test
	void shouldSaveExistingEntityAndRemoveApiKeys() {
		// arrange
		SecretEntity mockEntity = TestUtils.createSecretEntity();
		when(keystoreRepository.findByIdAndUserId(anyLong(), anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		when(repository.findById(1L)).thenReturn(Optional.of(mockEntity));
		when(converter.toEntity(any(SecretEntity.class), any(SaveSecretRequestDto.class))).thenReturn(mockEntity);
		when(repository.save(any(SecretEntity.class))).thenReturn(mockEntity);
		when(apiKeyRestrictionRepository.findAllByUserIdAndSecretId(anyLong(), anyLong())).thenReturn(
				List.of(TestUtils.createApiKeyRestrictionEntity(1L), TestUtils.createApiKeyRestrictionEntity(2L)));
		when(keystoreAliasRepository.findById(anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));

		// act
		SaveEntityResponseDto response = service.save(TestUtils.createSaveSecretRequestDto(1L, Set.of(1L)));

		// assert
		assertNotNull(response);
		assertEquals(1L, response.getEntityId());
		verify(keystoreRepository).findByIdAndUserId(anyLong(), anyLong());
		verify(repository).findById(1L);
		verify(converter).toEntity(any(SecretEntity.class), any(SaveSecretRequestDto.class));
		verify(cryptoService).encrypt(mockEntity);
		verify(keystoreAliasRepository).findById(anyLong());
	}

	@Test
	void shouldSaveExistingEntityAndUpdateApiKeys() {
		// arrange
		SecretEntity mockEntity = TestUtils.createSecretEntity();
		when(keystoreRepository.findByIdAndUserId(anyLong(), anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		when(repository.findById(1L)).thenReturn(Optional.of(mockEntity));
		when(converter.toEntity(any(SecretEntity.class), any(SaveSecretRequestDto.class))).thenReturn(mockEntity);
		when(repository.save(any(SecretEntity.class))).thenReturn(mockEntity);
		when(apiKeyRestrictionRepository.findAllByUserIdAndSecretId(anyLong(), anyLong())).thenReturn(
				List.of(TestUtils.createApiKeyRestrictionEntity(1L), TestUtils.createApiKeyRestrictionEntity(2L)));
		when(keystoreAliasRepository.findById(anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));

		// act
		SaveEntityResponseDto response = service
				.save(TestUtils.createSaveSecretRequestDto(1L, Sets.newHashSet(2L, 3L)));

		// assert
		assertNotNull(response);
		assertEquals(1L, response.getEntityId());
		verify(keystoreRepository).findByIdAndUserId(anyLong(), anyLong());
		verify(repository).findById(1L);
		verify(converter).toEntity(any(SecretEntity.class), any(SaveSecretRequestDto.class));
		verify(cryptoService).encrypt(mockEntity);
		verify(apiKeyRestrictionRepository, never()).delete(any(ApiKeyRestrictionEntity.class));
		verify(keystoreAliasRepository).findById(anyLong());
	}

	@Test
	void shouldSaveExistingEntityWithoutNewValue() {
		// arrange
		SecretEntity mockEntity = TestUtils.createSecretEntity();
		when(keystoreRepository.findByIdAndUserId(anyLong(), anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		when(repository.findById(1L)).thenReturn(Optional.of(mockEntity));
		when(converter.toEntity(any(SecretEntity.class), any(SaveSecretRequestDto.class))).thenReturn(mockEntity);
		when(repository.save(any(SecretEntity.class))).thenReturn(mockEntity);
		when(keystoreAliasRepository.findById(anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));

		// act
		SaveSecretRequestDto dto = TestUtils.createSaveSecretRequestDto(1L);
		dto.setValue(null);
		SaveEntityResponseDto response = service.save(dto);

		// assert
		assertNotNull(response);
		assertEquals(1L, response.getEntityId());
		verify(keystoreRepository).findByIdAndUserId(anyLong(), anyLong());
		verify(repository).findById(1L);
		verify(converter).toEntity(any(SecretEntity.class), any(SaveSecretRequestDto.class));
		verify(cryptoService, never()).encrypt(mockEntity);
		verify(keystoreAliasRepository).findById(anyLong());
	}

	@Test
	void shouldNotFindById() {
		// arrange
		when(repository.findById(1L)).thenReturn(Optional.empty());

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.getById(1L));

		// assert
		assertEquals(SecretServiceImpl.WRONG_ENTITY, exception.getMessage());
		verify(repository).findById(1L);
		verify(converter, never()).toDto(any(), anyList());
	}

	@Test
	void shouldFindById() {
		// arrange
		when(repository.findById(1L)).thenReturn(Optional.of(TestUtils.createSecretEntity()));
		when(converter.toDto(any(), anyList())).thenReturn(new SecretDto());
		when(keystoreAliasRepository.findById(anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));

		// act
		SecretDto response = service.getById(1L);

		// assert
		assertNotNull(response);
		verify(repository).findById(1L);
		verify(converter).toDto(any(), anyList());
	}

	@Test
	void shouldReturnList() {
		// arrange
		Page<SecretEntity> mockList = new PageImpl<>(Lists.newArrayList(TestUtils.createSecretEntity()));
		when(repository.findAllByUserId(anyLong(), any(Pageable.class))).thenReturn(mockList);
		when(converter.toDtoList(any())).thenReturn(new SecretListDto(Lists.newArrayList(TestUtils.createSecretDto())));

		// act
		SecretListDto response = service.list(new PagingDto("ASC", "id", 0, 10));

		// assert
		assertNotNull(response);
		assertEquals(1, response.getResultList().size());
		verify(converter).toDtoList(any());
	}

	@Test
	void shouldDelete() {
		// act
		service.delete(1L);

		// assert
		verify(repository).deleteById(1L);
	}

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void shouldToggleStatus(boolean enabled) {
		// arrange
		when(repository.findByIdAndUserId(anyLong(), anyLong()))
				.thenReturn(Optional.of(TestUtils.createSecretEntity()));

		// act
		service.toggleStatus(1L, enabled);

		// assert
		verify(repository).save(any());
		verify(repository).findByIdAndUserId(anyLong(), anyLong());
	}

	@Test
	void shouldNotToggleStatus() {

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.toggleStatus(3L, true));

		// assert
		assertEquals("Wrong entity!", exception.getMessage());
		verify(repository, never()).save(any());

		MDC.clear();
	}

	@Test
	void shouldNotGetSecretValue() {
		// arrange
		when(repository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.getSecretValue(1L));

		// assert
		assertEquals("Wrong entity!", exception.getMessage());
		verify(cryptoService, never()).decrypt(any());
	}

	@Test
	void shouldGetSecretValue() {
		// arrange
		when(repository.findByIdAndUserId(anyLong(), anyLong()))
				.thenReturn(Optional.of(TestUtils.createSecretEntity()));
		when(cryptoService.decrypt(any())).thenReturn("result");

		// act
		String response = service.getSecretValue(1L);

		// assert
		assertEquals("result", response);
		verify(cryptoService).decrypt(any());
	}

	@Test
	void shouldReturnCount() {
		// arrange
		when(repository.countByUserId(anyLong())).thenReturn(4L);

		// act
		LongValueDto response = service.count();

		// assert
		assertNotNull(response);
		assertEquals(4L, response.getValue());

		MDC.clear();
	}
}
