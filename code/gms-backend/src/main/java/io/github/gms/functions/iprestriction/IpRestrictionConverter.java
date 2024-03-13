package io.github.gms.functions.iprestriction;

import org.springframework.data.domain.Page;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface IpRestrictionConverter {

    IpRestrictionDto toDto(IpRestrictionEntity entity);

    IpRestrictionListDto toDtoList(Page<IpRestrictionEntity> results);
}
