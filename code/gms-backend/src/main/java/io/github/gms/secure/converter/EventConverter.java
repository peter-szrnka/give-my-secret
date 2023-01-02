package io.github.gms.secure.converter;

import io.github.gms.common.abstraction.GmsConverter;
import io.github.gms.common.entity.EventEntity;
import io.github.gms.secure.dto.EventDto;
import io.github.gms.secure.dto.EventListDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface EventConverter extends GmsConverter<EventListDto, EventEntity> {
	
	EventDto toDto(EventEntity entity);
}
