package io.github.gms.secure.converter;

import java.util.List;

import io.github.gms.secure.dto.SystemPropertyDto;
import io.github.gms.secure.dto.SystemPropertyListDto;
import io.github.gms.secure.entity.SystemPropertyEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface SystemPropertyConverter {

	SystemPropertyListDto toDtoList(List<SystemPropertyEntity> resultList);

	SystemPropertyEntity toEntity(SystemPropertyEntity entity, SystemPropertyDto dto);
}
