package io.github.gms.functions.iprestriction;

import io.github.gms.common.dto.PagingDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.types.GmsException;
import io.github.gms.common.util.ConverterUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "ipRestrictionCache")
public class IpRestrictionServiceImpl implements IpRestrictionService {

    private final Clock clock;
    private final IpRestrictionRepository repository;
    private final IpRestrictionConverter converter;

    @Override
    public SaveEntityResponseDto save(SaveIpRestrictionDto dto) {
        Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
        IpRestrictionEntity entity = new IpRestrictionEntity();

        if (dto.getId() != null) {
            entity.setId(dto.getId());
        } else {
            entity.setCreationDate(ZonedDateTime.now(clock));
        }

        entity.setAllow(dto.isAllow());
        entity.setStatus(dto.getStatus());
        entity.setIpPattern(dto.getIpPattern());
        entity.setSecretId(dto.getSecretId());
        entity.setUserId(userId);
        entity.setLastModified(ZonedDateTime.now(clock));

        entity = repository.save(entity);
        return new SaveEntityResponseDto(entity.getId());
    }

    @Override
    public IpRestrictionDto getById(Long id) {
        IpRestrictionEntity entity = repository.findById(id).orElseThrow(() -> new GmsException("Entity not found!"));
        return converter.toDto(entity);
    }

    @Override
    public IpRestrictionListDto list(PagingDto dto) {
        Page<IpRestrictionEntity> results = repository.findAll(ConverterUtils.createPageable(dto));
        return converter.toDtoList(results);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Cacheable
    public List<IpRestrictionPattern> getIpRestrictionsBySecret(Long userId, Long secretId) {
        return repository.getAllPatternData(userId, secretId);
    }
}