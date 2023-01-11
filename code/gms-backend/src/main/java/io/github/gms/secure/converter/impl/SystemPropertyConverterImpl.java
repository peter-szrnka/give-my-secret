package io.github.gms.secure.converter.impl;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
		return new SystemPropertyListDto(resultList.stream()
				.map(this::toDto)
				.collect(Collectors.toList()));
	}
	
	private SystemPropertyDto toDto(SystemPropertyEntity entity) {
		return SystemPropertyDto.builder()
				.key(entity.getKey().name())
				.value(entity.getValue())
				.lastModified(entity.getLastModified())
				.build();
	}

	@Override
	public SystemPropertyEntity toEntity(SystemPropertyEntity entity, SystemPropertyDto dto) {
		if (entity == null) {
			entity = new SystemPropertyEntity();
		}

		entity.setKey(SystemProperty.getByKey(dto.getKey()).orElseThrow(() -> new GmsException("Unknown system property!")));
		entity.setValue(dto.getValue());
		entity.setLastModified(LocalDateTime.now(clock));
		return entity;
	}
}