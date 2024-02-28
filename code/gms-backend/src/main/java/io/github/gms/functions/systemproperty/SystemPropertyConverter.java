package io.github.gms.functions.systemproperty;

import io.github.gms.functions.systemproperty.SystemPropertyDto;
import io.github.gms.functions.systemproperty.SystemPropertyListDto;
import io.github.gms.functions.systemproperty.SystemPropertyEntity;

import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface SystemPropertyConverter {

	SystemPropertyListDto toDtoList(List<SystemPropertyEntity> resultList);

	SystemPropertyEntity toEntity(SystemPropertyEntity entity, SystemPropertyDto dto);
}
