package io.github.gms.functions.event;

import io.github.gms.common.abstraction.AbstractController;
import io.github.gms.common.util.ConverterUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	@GetMapping(PATH_LIST)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_VIEWER')")
	public EventListDto list(
			@RequestParam("direction") String direction,
			@RequestParam("property") String property,
			@RequestParam("page") int page,
			@RequestParam("size") int size) {
		return service.list(ConverterUtils.createPageable(direction, property, page, size));
	}
	
	@PostMapping("/list/{userId}")
	@PreAuthorize(ROLE_ADMIN)
	public EventListDto listByUserId(@PathVariable(USER_ID) Long userId,
									 @RequestParam("direction") String direction,
									 @RequestParam("property") String property,
									 @RequestParam("page") int page,
									 @RequestParam("size") int size) {
		return service.listByUser(userId, ConverterUtils.createPageable(direction, property, page, size));
	}
}
