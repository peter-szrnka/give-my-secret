package io.github.gms.functions.event;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
public class EventConverter {

	public EventDto toDto(EventEntity entity, String username) {
		EventDto dto = new EventDto();
		dto.setId(entity.getId());
		dto.setEventDate(entity.getEventDate());
		dto.setOperation(entity.getOperation());
		dto.setTarget(entity.getTarget());
		dto.setUserId(entity.getUserId());
		dto.setUsername(username);
		return dto;
	}

	public EventListDto toDtoList(Page<EventEntity> resultList, String username) {
		List<EventDto> results = resultList.stream().map(entity -> toDto(entity, username)).toList();
		return EventListDto.builder().resultList(results).totalElements(resultList.getTotalElements()).build();
	}
}
