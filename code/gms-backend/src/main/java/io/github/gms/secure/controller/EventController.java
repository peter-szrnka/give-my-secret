package io.github.gms.secure.controller;

import io.github.gms.common.abstraction.AbstractController;
import io.github.gms.secure.dto.EventListDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.service.EventService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.github.gms.common.util.Constants.PATH_LIST;
import static io.github.gms.common.util.Constants.ROLE_ADMIN;
import static io.github.gms.common.util.Constants.USER_ID;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/secure/event")
public class EventController extends AbstractController<EventService> {

	public EventController(EventService service) {
		super(service);
	}

	@PostMapping(PATH_LIST)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_VIEWER')")
	public EventListDto list(@RequestBody PagingDto dto) {
		return service.list(dto);
	}
	
	@PostMapping("/list/{userId}")
	@PreAuthorize(ROLE_ADMIN)
	public EventListDto listByUserId(@PathVariable(USER_ID) Long userId, @RequestBody PagingDto dto) {
		return service.listByUser(userId, dto);
	}
}
