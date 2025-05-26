package io.github.gms.functions.event;

import io.github.gms.common.abstraction.AbstractController;
import io.github.gms.common.dto.IntegerValueDto;
import io.github.gms.common.util.ConverterUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static io.github.gms.common.util.Constants.*;

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
			@RequestParam(DIRECTION) String direction,
			@RequestParam(PROPERTY) String property,
			@RequestParam(PAGE) int page,
			@RequestParam(SIZE) int size) {
		return service.list(ConverterUtils.createPageable(direction, property, page, size));
	}
	
	@GetMapping("/list/{userId}")
	@PreAuthorize(ROLE_ADMIN)
	public EventListDto listByUserId(@PathVariable(USER_ID) Long userId,
									 @RequestParam(DIRECTION) String direction,
									 @RequestParam(PROPERTY) String property,
									 @RequestParam(PAGE) int page,
									 @RequestParam(SIZE) int size) {
		return service.listByUser(userId, ConverterUtils.createPageable(direction, property, page, size));
	}

	@GetMapping("/unprocessed")
	@PreAuthorize(ROLE_ADMIN)
	public ResponseEntity<IntegerValueDto> getUnprocessedEventsCount() {
		return ResponseEntity.ok(service.getUnprocessedEventsCount());
	}
}
