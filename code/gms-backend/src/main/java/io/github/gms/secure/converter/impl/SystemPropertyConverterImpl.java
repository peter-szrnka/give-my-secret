package io.github.gms.secure.converter.impl;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.exception.GmsException;
import io.github.gms.secure.converter.SystemPropertyConverter;
import io.github.gms.secure.dto.SystemPropertyDto;
import io.github.gms.secure.dto.SystemPropertyListDto;
import io.github.gms.secure.entity.SystemPropertyEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
public class SystemPropertyConverterImpl implements SystemPropertyConverter {
	
	private final Clock clock;
	
	public SystemPropertyConverterImpl(Clock clock) {
		this.clock = clock;
	}

	@Override
	public SystemPropertyListDto toDtoList(List<SystemPropertyEntity> resultList) {
		Map<SystemProperty, SystemPropertyEntity> propertyMap = resultList.stream()
			      .collect(Collectors.toMap(SystemPropertyEntity::getKey, Function.identity()));

		return new SystemPropertyListDto(Stream.of(SystemProperty.values())
				.map(property -> toDto(propertyMap, property))
				.collect(Collectors.toList()));
	}
	
	private SystemPropertyDto toDto(Map<SystemProperty, SystemPropertyEntity> propertyMap, SystemProperty property) {
		SystemPropertyEntity entity = propertyMap.get(property);
		ZonedDateTime lastModified = null;
		String value = property.getDefaultValue();
		boolean factoryValue = entity == null;
		
		if (entity != null) {
			lastModified = entity.getLastModified();
			value = entity.getValue();
		}

		return SystemPropertyDto.builder()
				.key(property.name())
				.value(value)
				.type(property.getType())
				.lastModified(lastModified)
				.factoryValue(factoryValue)
				.build();
	}

	@Override
	public SystemPropertyEntity toEntity(SystemPropertyEntity entity, SystemPropertyDto dto) {
		if (entity == null) {
			entity = new SystemPropertyEntity();
		}

		entity.setKey(SystemProperty.getByKey(dto.getKey()).orElseThrow(() -> new GmsException("Unknown system property!")));
		entity.setValue(dto.getValue());
		entity.setLastModified(ZonedDateTime.now(clock));
		return entity;
	}
}