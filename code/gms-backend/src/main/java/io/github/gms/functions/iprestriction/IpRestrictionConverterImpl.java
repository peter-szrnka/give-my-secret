package io.github.gms.functions.iprestriction;

import io.github.gms.common.model.IpRestrictionPattern;
import io.github.gms.common.util.MdcUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class IpRestrictionConverterImpl implements IpRestrictionConverter {

    private final Clock clock;

    @Override
    public List<IpRestrictionDto> toDtoList(List<IpRestrictionEntity> results) {
        return results.stream()
                        .map(IpRestrictionConverterImpl::toDto)
                        .toList();
    }

    @Override
    public List<IpRestrictionPattern> toModelList(List<IpRestrictionEntity> entities) {
        return entities.stream().map(IpRestrictionConverterImpl::toModel).toList();
    }

    @Override
    public IpRestrictionEntity toEntity(IpRestrictionDto dto) {
        IpRestrictionEntity entity = new IpRestrictionEntity();

        if (dto.getId() != null) {
            entity.setId(dto.getId());
            entity.setCreationDate(dto.getCreationDate());
        } else {
            entity.setCreationDate(ZonedDateTime.now(clock));
        }

        entity.setGlobal(dto.isGlobal());
        entity.setAllow(dto.isAllow());
        entity.setIpPattern(dto.getIpPattern());
        entity.setSecretId(dto.getSecretId());
        entity.setUserId(MdcUtils.getUserId());
        entity.setLastModified(ZonedDateTime.now(clock));

        return entity;
    }

    private static IpRestrictionDto toDto(IpRestrictionEntity entity) {
        return IpRestrictionDto.builder()
                .id(entity.getId())
                .allow(entity.isAllow())
                .ipPattern(entity.getIpPattern())
                .secretId(entity.getSecretId())
                .creationDate(entity.getCreationDate())
                .lastModified(entity.getLastModified())
                .global(entity.isGlobal())
                .build();
    }

    private static IpRestrictionPattern toModel(IpRestrictionEntity entity) {
        return new IpRestrictionPattern(entity.getIpPattern(), entity.isAllow());
    }
}
