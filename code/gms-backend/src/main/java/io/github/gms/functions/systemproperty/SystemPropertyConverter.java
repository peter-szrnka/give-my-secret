package io.github.gms.functions.systemproperty;

import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface SystemPropertyConverter {

	SystemPropertyListDto toDtoList(List<SystemPropertyEntity> resultList);

	SystemPropertyEntity toEntity(SystemPropertyEntity entity, SystemPropertyDto dto);
}
