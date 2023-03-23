package io.github.gms.secure.service.impl;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.exception.GmsException;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.common.util.MdcUtils;
import io.github.gms.secure.converter.ApiKeyConverter;
import io.github.gms.secure.dto.ApiKeyDto;
import io.github.gms.secure.dto.ApiKeyListDto;
import io.github.gms.secure.dto.IdNamePairListDto;
import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveApiKeyRequestDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.entity.ApiKeyEntity;
import io.github.gms.secure.repository.ApiKeyRepository;
import io.github.gms.secure.service.ApiKeyService;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static io.github.gms.common.util.Constants.CACHE_API;
import static io.github.gms.common.util.Constants.ENTITY_NOT_FOUND;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@CacheConfig(cacheNames = { CACHE_API })
public class ApiKeyServiceImpl implements ApiKeyService {

	private final ApiKeyRepository repository;
	private final ApiKeyConverter converter;

	public ApiKeyServiceImpl(ApiKeyRepository repository, ApiKeyConverter converter) {
		this.repository = repository;
		this.converter = converter;
	}

	@Override
	@CacheEvict(cacheNames = { CACHE_API }, allEntries = true)
	public SaveEntityResponseDto save(SaveApiKeyRequestDto dto) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		ApiKeyEntity entity;

		dto.setUserId(userId);
		validateNewApiKey(dto, dto.getId() == null ? 0 : 1);

		if (dto.getId() == null) {
			entity = converter.toNewEntity(dto);
		} else {
			entity = converter.toEntity(getApiKeyEntity(dto.getId(), userId), dto);
		}

		entity = repository.save(entity);
		return new SaveEntityResponseDto(entity.getId());
	}

	@Override
	public ApiKeyDto getById(Long id) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		return converter.toDto(getApiKeyEntity(id, userId));
	}

	@Override
	public ApiKeyListDto list(PagingDto dto) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));

		try {
			Page<ApiKeyEntity> resultList = repository.findAllByUserId(userId, ConverterUtils.createPageable(dto));
			return converter.toDtoList(resultList);
		} catch (Exception e) {
			return new ApiKeyListDto(Collections.emptyList());
		}
	}

	@Override
	@CacheEvict(cacheNames = { CACHE_API }, allEntries = true)
	public void delete(Long id) {
		repository.deleteById(id);
	}

	@Override
	@CacheEvict(cacheNames = { CACHE_API }, allEntries = true)
	public void toggleStatus(Long id, boolean enabled) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));

		ApiKeyEntity entity = getApiKeyEntity(id, userId);
		entity.setStatus(enabled ? EntityStatus.ACTIVE : EntityStatus.DISABLED);
		repository.save(entity);
	}

	@Override
	public String getDecryptedValue(Long id) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		return getApiKeyEntity(id, userId).getValue();
	}
	
	@Override
	public LongValueDto count() {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		return new LongValueDto(repository.countByUserId(userId));
	}

	@Override
	public IdNamePairListDto getAllApiKeyNames() {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		return new IdNamePairListDto(repository.getAllApiKeyNames(userId));
	}

	private ApiKeyEntity getApiKeyEntity(Long id, Long userId) {
		return repository.findByIdAndUserId(id, userId).orElseThrow(() -> new GmsException(ENTITY_NOT_FOUND));
	}
	
	private void validateNewApiKey(SaveApiKeyRequestDto dto, int expectedCount) {
		if (repository.countAllApiKeysByName(MdcUtils.getUserId(), dto.getName()) > expectedCount) {
			throw new GmsException("API key name must be unique!");
		}

		if (repository.countAllApiKeysByValue(MdcUtils.getUserId(), dto.getValue()) > expectedCount) {
			throw new GmsException("API key value must be unique!");
		}
	}
}
