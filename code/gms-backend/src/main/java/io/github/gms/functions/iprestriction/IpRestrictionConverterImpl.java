package io.github.gms.functions.iprestriction;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class IpRestrictionConverterImpl implements IpRestrictionConverter {

    @Override
    public IpRestrictionDto toDto(IpRestrictionEntity entity) {
        return IpRestrictionDto.builder()
                .id(entity.getId())
                .allow(entity.isAllow())
                .ipPattern(entity.getIpPattern())
                .status(entity.getStatus())
                .userId(entity.getUserId())
                .secretId(entity.getSecretId())
                .creationDate(entity.getCreationDate())
                .lastModified(entity.getLastModified())
                .build();
    }

    @Override
    public IpRestrictionListDto toDtoList(Page<IpRestrictionEntity> results) {
        return IpRestrictionListDto.builder()
                .resultList(results.toList().stream()
                        .map(this::toDto)
                        .toList())
                .totalElements(results.getTotalElements())
                .build();
    }
}
