package io.github.gms.secure.converter.impl;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import io.github.gms.secure.converter.EventConverter;
import io.github.gms.secure.dto.EventDto;
import io.github.gms.secure.dto.EventListDto;
import io.github.gms.secure.entity.EventEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
public class EventConverterImpl implements EventConverter {
	
	@Override
	public EventDto toDto(EventEntity entity) {
		EventDto dto = new EventDto();
		dto.setId(entity.getId());
		dto.setEventDate(entity.getEventDate());
		dto.setOperation(entity.getOperation());
		dto.setTarget(entity.getTarget());
		dto.setUserId(entity.getUserId());
		return dto;
	}

	@Override
	public EventListDto toDtoList(Page<EventEntity> resultList) {
		return new EventListDto(resultList.toList().stream().map(this::toDto).collect(Collectors.toList()));
	}
}
