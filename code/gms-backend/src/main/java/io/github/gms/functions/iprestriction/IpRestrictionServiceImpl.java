package io.github.gms.functions.iprestriction;

import io.github.gms.common.model.IpRestrictionPattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "ipRestrictionCache")
public class IpRestrictionServiceImpl implements IpRestrictionService {

    private final IpRestrictionRepository repository;
    private final IpRestrictionConverter converter;

    @Override
    public void updateIpRestrictionsForSecret(Long secretId, List<IpRestrictionDto> ipRestrictions) {
        ipRestrictions.forEach(ipRestriction -> ipRestriction.setSecretId(secretId));

        Set<Long> existingEntityIds = getAllBySecretId(secretId)
                .stream()
                .map(IpRestrictionDto::getId)
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
    @Cacheable
    public List<IpRestrictionPattern> getIpRestrictionsBySecret(Long secretId) {
        return converter.toModelList(findAll(secretId));
    }

    private List<IpRestrictionEntity> findAll(Long secretId) {
        return repository.findAllBySecretId(secretId);
    }
}