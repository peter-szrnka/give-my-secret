package io.github.gms.functions.iprestriction;

import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.model.IpRestrictionPatterns;
import io.github.gms.common.types.GmsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static io.github.gms.common.util.Constants.CACHE_GLOBAL_IP_RESTRICTION;
import static io.github.gms.common.util.Constants.CACHE_IP_RESTRICTION;
import static java.util.stream.Collectors.toSet;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = { CACHE_IP_RESTRICTION, CACHE_GLOBAL_IP_RESTRICTION })
public class IpRestrictionServiceImpl implements IpRestrictionService {

    private final IpRestrictionRepository repository;
    private final IpRestrictionConverter converter;

    @Override
    @CacheEvict(cacheNames = { CACHE_GLOBAL_IP_RESTRICTION }, allEntries = true)
    public SaveEntityResponseDto save(IpRestrictionDto dto) {
        if (dto.getId() != null && !dto.isGlobal()) {
            throw new GmsException("Only global IP restrictions allowed to save with this service!");
        }

        IpRestrictionEntity entity = converter.toEntity(dto);
        entity.setSecretId(null);
        entity.setUserId(null);
        entity.setGlobal(true);
        entity.setStatus(EntityStatus.ACTIVE);
        entity = repository.save(entity);
        return new SaveEntityResponseDto(entity.getId());
    }

    @Override
    public IpRestrictionListDto list(Pageable pageable) {
        Page<IpRestrictionEntity> results = repository.findAllGlobal(pageable);
        return converter.toDtoList(results);
    }

    @Override
    public IpRestrictionDto getById(Long id) {
        return converter.toDto(findAndValidateEntity(id));
    }

    @Override
    public void delete(Long id) {
        repository.delete(findAndValidateEntity(id));
    }

    @Override
    @CacheEvict(cacheNames = { CACHE_IP_RESTRICTION }, allEntries = true)
    public void updateIpRestrictionsForSecret(Long secretId, List<IpRestrictionDto> ipRestrictions) {
        ipRestrictions.forEach(ipRestriction -> ipRestriction.setSecretId(secretId));

        Set<Long> existingEntityIds = findAll(secretId)
                .stream()
                .map(IpRestrictionEntity::getId)
                .collect(toSet());
        Set<Long> newIds = ipRestrictions.stream()
                .map(IpRestrictionDto::getId)
                .filter(Objects::nonNull)
                .collect(toSet());

        // Save each entity
        ipRestrictions.forEach(dto -> repository.save(converter.toEntity(dto)));

        // Remove old entities
        repository.deleteAllById(existingEntityIds.stream().filter(id -> !newIds.contains(id)).collect(toSet()));
    }

    @Override
    public List<IpRestrictionDto> getAllBySecretId(Long secretId) {
        return converter.toDtoList(findAll(secretId));
    }

    @Override
    @Cacheable(cacheNames = CACHE_IP_RESTRICTION)
    public IpRestrictionPatterns checkIpRestrictionsBySecret(Long secretId) {
        return converter.toModel(findAll(secretId));
    }

    @Override
    @Cacheable(cacheNames = CACHE_GLOBAL_IP_RESTRICTION)
    public IpRestrictionPatterns checkGlobalIpRestrictions() {
        return converter.toModel(repository.findAllGlobal());
    }

    @Override
    @CacheEvict(cacheNames = { CACHE_GLOBAL_IP_RESTRICTION }, allEntries = true)
    public void toggleStatus(Long id, boolean enabled) {
        IpRestrictionEntity entity = findAndValidateEntity(id);
        entity.setStatus(enabled ? EntityStatus.ACTIVE : EntityStatus.DISABLED);
        repository.save(entity);
    }

    private IpRestrictionEntity findAndValidateEntity(Long id) {
        IpRestrictionEntity entity = repository.findById(id).orElseThrow(() -> new GmsException("Entity not found!"));

        if (!entity.isGlobal()) {
            throw new GmsException("Invalid request, the given resource is not a global IP restriction!");
        }

        return entity;
    }

    private List<IpRestrictionEntity> findAll(Long secretId) {
        return repository.findAllBySecretId(secretId);
    }
}