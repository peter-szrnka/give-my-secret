package io.github.gms.functions.iprestriction;

import io.github.gms.common.model.IpRestrictionPattern;
import io.github.gms.common.model.IpRestrictionPatterns;
import io.github.gms.common.util.MdcUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
public class IpRestrictionConverter {

    private final Clock clock;

    public List<IpRestrictionDto> toDtoList(List<IpRestrictionEntity> results) {
        return results.stream()
                .map(this::toDto)
                .toList();
    }

    public IpRestrictionPatterns toModel(List<IpRestrictionEntity> entities) {
        return new IpRestrictionPatterns(entities.stream().map(IpRestrictionConverter::toModel).toList());
    }

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
        entity.setStatus(dto.getStatus());
        entity.setLastModified(ZonedDateTime.now(clock));

        return entity;
    }

    public IpRestrictionDto toDto(IpRestrictionEntity entity) {
        return IpRestrictionDto.builder()
                .id(entity.getId())
                .allow(entity.isAllow())
                .ipPattern(entity.getIpPattern())
                .secretId(entity.getSecretId())
                .status(entity.getStatus())
                .creationDate(entity.getCreationDate())
                .lastModified(entity.getLastModified())
                .global(entity.isGlobal())
                .build();
    }

    public IpRestrictionListDto toDtoList(Page<IpRestrictionEntity> results) {
        return IpRestrictionListDto.builder()
                .resultList(results.toList().stream()
                        .map(this::toDto)
                        .toList())
                .totalElements(results.getTotalElements())
                .build();
    }

    private static IpRestrictionPattern toModel(IpRestrictionEntity entity) {
        return new IpRestrictionPattern(entity.getIpPattern(), entity.isAllow());
    }
}
