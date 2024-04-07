package io.github.gms.functions.event;

import io.github.gms.common.abstraction.GmsService;
import io.github.gms.common.dto.PagingDto;
import io.github.gms.common.model.UserEvent;
import org.springframework.data.domain.Pageable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface EventService extends GmsService {

	void saveUserEvent(UserEvent event);
	
	EventListDto list(Pageable pageable);
	
	EventListDto listByUser(Long userId, PagingDto dto);
}
