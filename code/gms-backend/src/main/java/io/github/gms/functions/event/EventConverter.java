package io.github.gms.functions.event;

import io.github.gms.functions.event.EventDto;
import io.github.gms.functions.event.EventListDto;
import io.github.gms.functions.event.EventEntity;
import org.springframework.data.domain.Page;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface EventConverter {
	
	EventDto toDto(EventEntity entity, String username);
	
	EventListDto toDtoList(Page<EventEntity> resultList, String username);
}
