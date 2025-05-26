package io.github.gms.functions.event;

import io.github.gms.common.abstraction.BatchDeletionService;
import io.github.gms.common.abstraction.GmsService;
import io.github.gms.common.dto.IntegerValueDto;
import io.github.gms.common.model.UserEvent;
import org.springframework.data.domain.Pageable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface EventService extends GmsService, BatchDeletionService {

	void saveUserEvent(UserEvent event);
	
	EventListDto list(Pageable pageable);
	
	EventListDto listByUser(Long userId, Pageable pageable);

    IntegerValueDto getUnprocessedEventsCount();
}
