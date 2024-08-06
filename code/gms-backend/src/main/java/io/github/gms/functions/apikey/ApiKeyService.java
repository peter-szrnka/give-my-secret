package io.github.gms.functions.apikey;

import io.github.gms.common.abstraction.AbstractCrudService;
import io.github.gms.common.abstraction.BatchDeletionService;
import io.github.gms.common.dto.IdNamePairListDto;
import io.github.gms.common.dto.LongValueDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.service.CountService;
import io.github.gms.common.types.GmsException;
import io.github.gms.common.util.MdcUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

import static io.github.gms.common.types.ErrorCode.GMS_002;
import static io.github.gms.common.types.ErrorCode.GMS_018;
import static io.github.gms.common.types.ErrorCode.GMS_019;
import static io.github.gms.common.util.Constants.CACHE_API;
import static io.github.gms.common.util.Constants.ENTITY_NOT_FOUND;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = { CACHE_API })
public class ApiKeyService implements AbstractCrudService<SaveApiKeyRequestDto, SaveEntityResponseDto, ApiKeyDto, ApiKeyListDto>,
CountService, BatchDeletionService {

    private final ApiKeyRepository repository;
    private final ApiKeyConverter converter;

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
    public ApiKeyListDto list(Pageable pageable) {
        Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));

        try {
            Page<ApiKeyEntity> resultList = repository.findAllByUserId(userId, pageable);
            return converter.toDtoList(resultList);
        } catch (Exception e) {
            return ApiKeyListDto.builder().resultList(Collections.emptyList()).totalElements(0).build();
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

    public String getDecryptedValue(Long id) {
        Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
        return getApiKeyEntity(id, userId).getValue();
    }

    @Override
    public LongValueDto count() {
        Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
        return new LongValueDto(repository.countByUserId(userId));
    }

    public IdNamePairListDto getAllApiKeyNames() {
        Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
        return new IdNamePairListDto(repository.getAllApiKeyNames(userId));
    }

    @Async
    @Override
    public void batchDeleteByUserIds(Set<Long> userIds) {
        repository.deleteAllByUserId(userIds);
        log.info("All API keys have been removed for the requested users");
    }

    private ApiKeyEntity getApiKeyEntity(Long id, Long userId) {
        return repository.findByIdAndUserId(id, userId).orElseThrow(() -> new GmsException(ENTITY_NOT_FOUND, GMS_002));
    }

    private void validateNewApiKey(SaveApiKeyRequestDto dto, int expectedCount) {
        if (repository.countAllApiKeysByName(MdcUtils.getUserId(), dto.getName()) > expectedCount) {
            throw new GmsException("API key name must be unique!", GMS_018);
        }

        if (repository.countAllApiKeysByValue(MdcUtils.getUserId(), dto.getValue()) > expectedCount) {
            throw new GmsException("API key value must be unique!", GMS_019);
        }
    }
}
