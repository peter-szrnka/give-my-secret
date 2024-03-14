package io.github.gms.functions.iprestriction;

import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface IpRestrictionConverter {

    List<IpRestrictionDto> toDtoList(List<IpRestrictionEntity> results);

    List<IpRestrictionPattern> toModelList(List<IpRestrictionEntity> entities);

    IpRestrictionEntity toEntity(IpRestrictionDto dto);
}
