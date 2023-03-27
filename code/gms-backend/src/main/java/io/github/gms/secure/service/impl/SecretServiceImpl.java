package io.github.gms.secure.service.impl;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.SecretType;
import io.github.gms.common.exception.GmsException;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.common.util.MdcUtils;
import io.github.gms.secure.converter.SecretConverter;
import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.dto.SaveSecretRequestDto;
import io.github.gms.secure.dto.SecretDto;
import io.github.gms.secure.dto.SecretListDto;
import io.github.gms.secure.entity.ApiKeyRestrictionEntity;
import io.github.gms.secure.entity.KeystoreAliasEntity;
import io.github.gms.secure.entity.SecretEntity;
import io.github.gms.secure.repository.ApiKeyRestrictionRepository;
import io.github.gms.secure.repository.KeystoreAliasRepository;
import io.github.gms.secure.repository.KeystoreRepository;
import io.github.gms.secure.repository.SecretRepository;
import io.github.gms.secure.service.CryptoService;
import io.github.gms.secure.service.SecretService;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.gms.common.util.Constants.CACHE_API;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@CacheConfig(cacheNames = { CACHE_API })
public class SecretServiceImpl implements SecretService {

	static final String WRONG_ENTITY = "Wrong entity!";
	static final String PLEASE_PROVIDE_ACTIVE_KEYSTORE = "Please provide an active keystore";
	static final String WRONG_KEYSTORE_ALIAS = "Wrong keystore alias!";

	private final CryptoService cryptoService;
	private final KeystoreRepository keystoreRepository;
	private final KeystoreAliasRepository keystoreAliasRepository;
	private final SecretRepository repository;
	private final SecretConverter converter;
	private final ApiKeyRestrictionRepository apiKeyRestrictionRepository;

	public SecretServiceImpl(
			CryptoService cryptoService,
			KeystoreRepository keystoreRepository,
			KeystoreAliasRepository keystoreAliasRepository,
			SecretRepository repository,
			SecretConverter converter,
			ApiKeyRestrictionRepository apiKeyRestrictionRepository) {
		this.cryptoService = cryptoService;
		this.keystoreRepository = keystoreRepository;
		this.keystoreAliasRepository = keystoreAliasRepository;
		this.repository = repository;
		this.converter = converter;
		this.apiKeyRestrictionRepository = apiKeyRestrictionRepository;
	}

	@Override
	@CacheEvict(cacheNames = { CACHE_API }, allEntries = true)
	public SaveEntityResponseDto save(SaveSecretRequestDto dto) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		SecretEntity entity;
		dto.setUserId(userId);

		validateKeystore(dto);
		validateSecret(dto, dto.getId() == null ? 0 : 1);

		if (dto.getId() == null) {
			entity = converter.toNewEntity(dto);
		} else {
			Optional<SecretEntity> opionalEntity = repository.findById(dto.getId());

			if (opionalEntity.isEmpty()) {
				throw new GmsException("Secret not found!");
			}

			entity = converter.toEntity(opionalEntity.get(), dto);
		}
		
		if (dto.getId() == null || dto.getValue() != null) {
			cryptoService.encrypt(entity);
		}

		entity = repository.save(entity);

		updateApiRestrictions(entity, dto.getApiKeyRestrictions());
		return new SaveEntityResponseDto(entity.getId());
	}

	@Override
	public SecretDto getById(Long id) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		SecretEntity entity = repository.findById(id).orElseThrow(() -> new GmsException(WRONG_ENTITY));
		
		List<ApiKeyRestrictionEntity> result = apiKeyRestrictionRepository.findAllByUserIdAndSecretId(userId, entity.getId());
		
		SecretDto response = converter.toDto(entity, result);
		
		// Let's add the keystore ID
		keystoreAliasRepository.findById(entity.getKeystoreAliasId())
			.ifPresent(keystoreAlias -> response.setKeystoreId(keystoreAlias.getKeystoreId()));

		return response;
	}

	@Override
	public SecretListDto list(PagingDto dto) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		Page<SecretEntity> resultList = repository.findAllByUserId(userId, ConverterUtils.createPageable(dto));
		return converter.toDtoList(resultList);
	}

	@Override
	public void delete(Long id) {
		repository.deleteById(id);
	}

	@Override
	@CacheEvict(cacheNames = { CACHE_API }, allEntries = true)
	public void toggleStatus(Long id, boolean enabled) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		Optional<SecretEntity> entityOptionalResult = repository.findByIdAndUserId(id, userId);

		SecretEntity entity = entityOptionalResult.orElseThrow(() -> new GmsException(WRONG_ENTITY));
		entity.setStatus(enabled ? EntityStatus.ACTIVE : EntityStatus.DISABLED);
		repository.save(entity);
	}

	@Override
	public String getSecretValue(Long id) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		Optional<SecretEntity> entityOptionalResult = repository.findByIdAndUserId(id, userId);

		SecretEntity entity = entityOptionalResult.orElseThrow(() -> new GmsException(WRONG_ENTITY));
		return cryptoService.decrypt(entity);
	}

	@Override
	public LongValueDto count() {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		return new LongValueDto(repository.countByUserId(userId));
	}

	private void updateApiRestrictions(SecretEntity entity, Set<Long> apiKeys) {
		// All entities
		Set<Long> existingEntities = apiKeyRestrictionRepository
				.findAllByUserIdAndSecretId(entity.getUserId(), entity.getId()).stream()
				.map(ApiKeyRestrictionEntity::getApiKeyId)
				.collect(Collectors.toSet());
		
		// Add new entities
		apiKeys.stream().filter(apiKey -> !existingEntities.contains(apiKey)).forEach(apiKey -> {
			ApiKeyRestrictionEntity newEntity = new ApiKeyRestrictionEntity();
			newEntity.setSecretId(entity.getId());
			newEntity.setUserId(entity.getUserId());
			newEntity.setApiKeyId(apiKey);
			apiKeyRestrictionRepository.save(newEntity);
		});

		// Remove old entities
		existingEntities.stream()
			.filter(existingEntityId -> !apiKeys.contains(existingEntityId))
			.forEach(existingEntityId -> apiKeyRestrictionRepository.deleteByUserIdAndSecretIdAndApiKeyId(entity.getUserId(), entity.getId(), existingEntityId));
	}

	private void validateKeystore(SaveSecretRequestDto dto) {
		if (dto.getKeystoreAliasId() == null) {
			throw new GmsException(WRONG_KEYSTORE_ALIAS);
		}
		
		KeystoreAliasEntity keystoreAlias = keystoreAliasRepository.findById(dto.getKeystoreAliasId()).orElseThrow(() -> new GmsException(WRONG_KEYSTORE_ALIAS));

		keystoreRepository.findByIdAndUserId(keystoreAlias.getKeystoreId(), dto.getUserId()).ifPresentOrElse(entity -> {
			if (EntityStatus.DISABLED == entity.getStatus()) {
				throw new GmsException(PLEASE_PROVIDE_ACTIVE_KEYSTORE);
			}
		}, () -> {
			throw new GmsException(WRONG_ENTITY);
		});
		
		if (!dto.getKeystoreId().equals(keystoreAlias.getKeystoreId())) {
			throw new GmsException("Invalid keystore defined in the request!");
		}
	}
	
	private void validateSecret(SaveSecretRequestDto dto, int expectedCount) {
		long secretIdCount = repository.countAllSecretsByUserIdAndSecretId(MdcUtils.getUserId(), dto.getSecretId());

		if (secretIdCount > expectedCount) {
			throw new GmsException("Secret ID name must be unique!");
		}
		
		if (SecretType.MULTIPLE_CREDENTIAL != dto.getType()) {
			return;
		}

		if (StringUtils.hasLength(dto.getValue()) && itemsNotValid(dto.getValue())) {
			throw new GmsException("Username password pair is invalid!");
		}
	}
	
	private boolean itemsNotValid(String value) {
		return Stream.of(value.split(";")).anyMatch(item -> !item.contains(":") || item.split(":").length != 2);
	}
}
