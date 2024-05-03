package io.github.gms.functions.secret;

import io.github.gms.common.dto.LongValueDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.SecretType;
import io.github.gms.common.service.CryptoService;
import io.github.gms.common.types.GmsException;
import io.github.gms.functions.iprestriction.IpRestrictionDto;
import io.github.gms.functions.iprestriction.IpRestrictionService;
import io.github.gms.functions.keystore.KeystoreAliasEntity;
import io.github.gms.functions.keystore.KeystoreAliasRepository;
import io.github.gms.functions.keystore.KeystoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.gms.common.types.ErrorCode.GMS_002;
import static io.github.gms.common.types.ErrorCode.GMS_007;
import static io.github.gms.common.types.ErrorCode.GMS_008;
import static io.github.gms.common.types.ErrorCode.GMS_015;
import static io.github.gms.common.types.ErrorCode.GMS_020;
import static io.github.gms.common.types.ErrorCode.GMS_021;
import static io.github.gms.common.util.Constants.CACHE_API;
import static io.github.gms.common.util.Constants.CACHE_IP_RESTRICTION;
import static io.github.gms.common.util.MdcUtils.getUserId;
import static java.util.stream.Collectors.toSet;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
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
	private final IpRestrictionService ipRestrictionService;

	@Override
	@Transactional
	@CacheEvict(cacheNames = { CACHE_API, CACHE_IP_RESTRICTION }, allEntries = true)
	public SaveEntityResponseDto save(SaveSecretRequestDto dto) {
		SecretEntity entity;
		dto.setUserId(getUserId());

		validateKeystore(dto);
		validateSecret(dto, dto.getId() == null ? 0 : 1);

		if (dto.getId() == null) {
			entity = converter.toNewEntity(dto);
		} else {
			entity = repository.findById(dto.getId())
					.orElseThrow(() -> new GmsException("Secret not found!", GMS_002));
			entity = converter.toEntity(entity, dto);
		}
		
		if (dto.getId() == null || StringUtils.hasText(dto.getValue())) {
			cryptoService.encrypt(entity);
		}

		entity = repository.save(entity);

		updateApiRestrictions(entity, dto.getApiKeyRestrictions());
		updateIpRestrictions(entity, dto.getIpRestrictions());
		return new SaveEntityResponseDto(entity.getId());
	}

	@Override
	public SecretDto getById(Long id) {
		SecretEntity entity = repository.findById(id).orElseThrow(() -> new GmsException(WRONG_ENTITY, GMS_002));
		
		List<ApiKeyRestrictionEntity> apiKeyRestrictions = apiKeyRestrictionRepository.findAllByUserIdAndSecretId(getUserId(), entity.getId());
		List<IpRestrictionDto> ipRestrictions = ipRestrictionService.getAllBySecretId(entity.getId());
		
		SecretDto response = converter.toDto(entity);
		response.setApiKeyRestrictions(apiKeyRestrictions.stream().map(ApiKeyRestrictionEntity::getApiKeyId).collect(Collectors.toSet()));
		response.setIpRestrictions(ipRestrictions);
		
		// Let's add the keystore ID
		keystoreAliasRepository.findById(entity.getKeystoreAliasId())
			.ifPresent(keystoreAlias -> response.setKeystoreId(keystoreAlias.getKeystoreId()));

		return response;
	}

	@Override
	public SecretListDto list(Pageable pageable) {
		Page<SecretEntity> resultList = repository.findAllByUserId(getUserId(), pageable);
		return converter.toDtoList(resultList);
	}

	@Override
	public void delete(Long id) {
		repository.deleteById(id);
	}

	@Override
	@CacheEvict(cacheNames = { CACHE_API }, allEntries = true)
	public void toggleStatus(Long id, boolean enabled) {
		Optional<SecretEntity> entityOptionalResult = repository.findByIdAndUserId(id, getUserId());

		SecretEntity entity = entityOptionalResult.orElseThrow(() -> new GmsException(WRONG_ENTITY, GMS_002));
		entity.setStatus(enabled ? EntityStatus.ACTIVE : EntityStatus.DISABLED);
		repository.save(entity);
	}

	@Override
	public String getSecretValue(Long id) {
		Optional<SecretEntity> entityOptionalResult = repository.findByIdAndUserId(id, getUserId());

		SecretEntity entity = entityOptionalResult.orElseThrow(() -> new GmsException(WRONG_ENTITY, GMS_002));
		return cryptoService.decrypt(entity);
	}

	@Override
	public LongValueDto count() {
		return new LongValueDto(repository.countByUserId(getUserId()));
	}

	@Async
	@Override
	public void batchDeleteByUserIds(Set<Long> userIds) {
		repository.deleteAllByUserId(userIds);
		log.info("All secrets have been removed for the requested users");
	}

	private void updateApiRestrictions(SecretEntity entity, Set<Long> apiKeys) {
		// All entities
		Set<Long> existingEntities = apiKeyRestrictionRepository
				.findAllByUserIdAndSecretId(entity.getUserId(), entity.getId()).stream()
				.map(ApiKeyRestrictionEntity::getApiKeyId)
				.collect(toSet());
		
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

	private void updateIpRestrictions(SecretEntity entity, List<IpRestrictionDto> ipRestrictions) {
		ipRestrictionService.updateIpRestrictionsForSecret(entity.getId(), ipRestrictions);
	}

	private void validateKeystore(SaveSecretRequestDto dto) {
		if (dto.getKeystoreAliasId() == null) {
			throw new GmsException(WRONG_KEYSTORE_ALIAS, GMS_008);
		}
		
		KeystoreAliasEntity keystoreAlias = keystoreAliasRepository.findById(dto.getKeystoreAliasId()).orElseThrow(() ->
				new GmsException(WRONG_KEYSTORE_ALIAS, GMS_008));

		keystoreRepository.findByIdAndUserId(keystoreAlias.getKeystoreId(), dto.getUserId()).ifPresentOrElse(entity -> {
			if (EntityStatus.DISABLED == entity.getStatus()) {
				throw new GmsException(PLEASE_PROVIDE_ACTIVE_KEYSTORE, GMS_015);
			}
		}, () -> {
			throw new GmsException(WRONG_ENTITY, GMS_002);
		});
		
		if (!dto.getKeystoreId().equals(keystoreAlias.getKeystoreId())) {
			throw new GmsException("Invalid keystore defined in the request!", GMS_007);
		}
	}
	
	private void validateSecret(SaveSecretRequestDto dto, int expectedCount) {
		long secretIdCount = repository.countAllSecretsByUserIdAndSecretId(getUserId(), dto.getSecretId());

		if (secretIdCount > expectedCount) {
			throw new GmsException("Secret ID name must be unique!", GMS_020);
		}
		
		if (SecretType.MULTIPLE_CREDENTIAL != dto.getType()) {
			return;
		}

		if (StringUtils.hasLength(dto.getValue()) && itemsNotValid(dto.getValue())) {
			throw new GmsException("Username password pair is invalid!", GMS_021);
		}
	}
	
	private static boolean itemsNotValid(String value) {
		return Stream.of(value.split(";")).anyMatch(item -> !item.contains(":") || item.split(":").length != 2);
	}
}
