package io.github.gms.functions.systemproperty;

import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.types.GmsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.gms.common.types.ErrorCode.GMS_026;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class SystemPropertyConverter {

	private final Clock clock;

	public SystemPropertyListDto toDtoList(List<SystemPropertyEntity> resultList) {
		Map<SystemProperty, SystemPropertyEntity> propertyMap = resultList.stream()
				.collect(Collectors.toMap(SystemPropertyEntity::getKey, Function.identity()));

		List<SystemPropertyDto> results = Stream.of(SystemProperty.values())
				.map(property -> toDto(propertyMap, property))
				.toList();
		return SystemPropertyListDto.builder()
				.resultList(results)
				.totalElements(results.size())
				.build();
	}

	public SystemPropertyEntity toEntity(SystemPropertyEntity entity, SystemPropertyDto dto) {
		if (entity == null) {
			entity = new SystemPropertyEntity();
		}

		entity.setKey(SystemProperty.getByKey(dto.getKey()).orElseThrow(() -> new GmsException("Unknown system property!", GMS_026)));
		entity.setValue(dto.getValue());
		entity.setLastModified(ZonedDateTime.now(clock));
		return entity;
	}


	private static SystemPropertyDto toDto(Map<SystemProperty, SystemPropertyEntity> propertyMap, SystemProperty property) {
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
				.category(property.getCategory())
				.lastModified(lastModified)
				.factoryValue(factoryValue)
				.build();
	}
}
