package io.github.gms.functions.systemproperty;

import io.github.gms.common.abstraction.GmsController;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;
import io.github.gms.common.util.ConverterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static io.github.gms.common.util.Constants.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/secure/system_property")
@AuditTarget(EventTarget.SYSTEM_PROPERTY)
public class SystemPropertyController implements GmsController {

	private final SystemPropertyService service;

	@PostMapping
	@PreAuthorize(ROLE_ADMIN)
	@Audited(operation = EventOperation.SAVE)
	public ResponseEntity<Void> save(@RequestBody SystemPropertyDto dto) {
		service.save(dto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@DeleteMapping("/{key}")
	@PreAuthorize(ROLE_ADMIN)
	@Audited(operation = EventOperation.DELETE)
	public ResponseEntity<String> delete(@PathVariable(KEY) String key) {
		service.delete(key);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping(PATH_LIST)
	@PreAuthorize(ROLE_ADMIN)
	public SystemPropertyListDto list(
			@RequestParam(DIRECTION) String direction,
			@RequestParam(PROPERTY) String property,
			@RequestParam(PAGE) int page,
			@RequestParam(SIZE) int size) {
		return service.list(ConverterUtils.createPageable(direction, property, page, size));
	}
}