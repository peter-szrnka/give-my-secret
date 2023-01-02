package io.github.gms.secure.service;

import io.github.gms.common.abstraction.GmsService;
import io.github.gms.secure.dto.EventListDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.model.UserEvent;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface EventService extends GmsService {

	void saveUserEvent(UserEvent event);
	
	EventListDto list(PagingDto dto);
}
