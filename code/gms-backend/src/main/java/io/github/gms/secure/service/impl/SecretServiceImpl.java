package io.github.gms.secure.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import io.github.gms.common.entity.ApiKeyRestrictionEntity;
import io.github.gms.common.entity.SecretEntity;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.exception.GmsException;
import io.github.gms.secure.converter.SecretConverter;
import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.dto.SaveSecretRequestDto;
import io.github.gms.secure.dto.SecretDto;
import io.github.gms.secure.dto.SecretListDto;
import io.github.gms.secure.repository.ApiKeyRestrictionRepository;
import io.github.gms.secure.repository.KeystoreRepository;
import io.github.gms.secure.repository.SecretRepository;
import io.github.gms.secure.service.CryptoService;
import io.github.gms.secure.service.SecretService;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
public class SecretServiceImpl implements SecretService {

	static final String WRONG_ENTITY = "Wrong entity!";
	static final String PLEASE_PROVIDE_ACTIVE_KEYSTORE = "Please provide an active keystore";
	static final String KEYSTORE_ID_NOT_FOUND = "Keystore not found!";

	@Autowired
	private CryptoService cryptoService;

	@Autowired
	private KeystoreRepository keystoreRepository;

	@Autowired
	private SecretRepository repository;

	@Autowired
	private SecretConverter converter;

	@Autowired
	private ApiKeyRestrictionRepository apiKeyRestrictionRepository;

	@Override
	public SaveEntityResponseDto save(SaveSecretRequestDto dto) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		SecretEntity entity;
		dto.setUserId(userId);

		validateKeystore(dto, userId);

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
		Optional<SecretEntity> entityOptionalResult = repository.findById(id);
		
		if (entityOptionalResult.isEmpty()) {
			throw new GmsException(WRONG_ENTITY);
		}
		
		List<ApiKeyRestrictionEntity> result = apiKeyRestrictionRepository.findAllByUserIdAndSecretId(userId, entityOptionalResult.get().getId());

		return converter.toDto(entityOptionalResult.get(), result);
	}

	@Override
	public SecretListDto list(PagingDto dto) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		Sort sort = Sort.by(Direction.valueOf(dto.getDirection()), dto.getProperty());
		Pageable pagingRequest = PageRequest.of(dto.getPage(), dto.getSize(), sort);

		Page<SecretEntity> resultList = repository.findAllByUserId(userId, pagingRequest);
		return converter.toDtoList(resultList);
	}

	@Override
	public void delete(Long id) {
		repository.deleteById(id);
	}

	@Override
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

	private void validateKeystore(SaveSecretRequestDto dto, Long userId) {
		if (dto.getKeystoreId() == null) {
			throw new GmsException(KEYSTORE_ID_NOT_FOUND);
		}

		keystoreRepository.findByIdAndUserId(dto.getKeystoreId(), userId).ifPresentOrElse(entity -> {
			if (EntityStatus.DISABLED == entity.getStatus()) {
				throw new GmsException(PLEASE_PROVIDE_ACTIVE_KEYSTORE);
			}
		}, () -> {
			throw new GmsException(WRONG_ENTITY);
		});
	}
}
