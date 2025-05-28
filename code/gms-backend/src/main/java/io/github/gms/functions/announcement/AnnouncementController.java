package io.github.gms.functions.announcement;

import io.github.gms.common.abstraction.AbstractController;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;
import io.github.gms.common.util.ConverterUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static io.github.gms.common.util.Constants.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/secure/announcement")
@AuditTarget(EventTarget.ANNOUNCEMENT)
public class AnnouncementController extends AbstractController<AnnouncementService> {

	public AnnouncementController(AnnouncementService service) {
		super(service);
	}

	@PostMapping
	@PreAuthorize(ROLE_ADMIN)
	@Audited(operation = EventOperation.SAVE)
	public SaveEntityResponseDto save(@RequestBody SaveAnnouncementDto dto) {
		return service.save(dto);
	}
	
	@GetMapping(PATH_VARIABLE_ID)
	@PreAuthorize(ALL_ROLE)
	@Audited(operation = EventOperation.GET_BY_ID)
	public AnnouncementDto getById(@PathVariable(ID) Long id) {
		return service.getById(id);
	}
	
	@GetMapping(PATH_LIST)
	@PreAuthorize(ALL_ROLE)
	public AnnouncementListDto list(
			@RequestParam(DIRECTION) String direction,
			@RequestParam(PROPERTY) String property,
			@RequestParam(PAGE) int page,
			@RequestParam(SIZE) int size) {
		return service.list(ConverterUtils.createPageable(direction, property, page, size));
	}
	
	@DeleteMapping(PATH_VARIABLE_ID)
	@PreAuthorize(ROLE_ADMIN)
	@Audited(operation = EventOperation.DELETE)
	public ResponseEntity<String> delete(@PathVariable(ID) Long id) {
		service.delete(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
