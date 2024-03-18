package io.github.gms.functions.iprestriction;

import io.github.gms.common.model.IpRestrictionPatterns;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface IpRestrictionConverter {

    List<IpRestrictionDto> toDtoList(List<IpRestrictionEntity> results);

    IpRestrictionPatterns toModel(List<IpRestrictionEntity> entities);

    IpRestrictionEntity toEntity(IpRestrictionDto dto);

    IpRestrictionDto toDto(IpRestrictionEntity entity);

    IpRestrictionListDto toDtoList(Page<IpRestrictionEntity> results);
}
