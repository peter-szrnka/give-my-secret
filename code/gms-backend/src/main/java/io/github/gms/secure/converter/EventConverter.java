package io.github.gms.secure.converter;

import java.util.List;

import io.github.gms.secure.dto.EventDto;
import io.github.gms.secure.dto.EventListDto;
import io.github.gms.secure.entity.EventEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface EventConverter {
	
	EventDto toDto(EventEntity entity, String username);
	
	EventListDto toDtoList(List<EventEntity> resultList, String username);
}
