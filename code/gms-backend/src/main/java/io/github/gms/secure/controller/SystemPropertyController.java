package io.github.gms.secure.controller;

import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;
import io.github.gms.common.util.Constants;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SystemPropertyDto;
import io.github.gms.secure.dto.SystemPropertyListDto;
import io.github.gms.secure.service.SystemPropertyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/secure/system_property")
@AuditTarget(EventTarget.SYSTEM_PROPERTY)
public class SystemPropertyController {

	private final SystemPropertyService service;

	public SystemPropertyController(SystemPropertyService service) {
		this.service = service;
	}

	@PostMapping
	@PreAuthorize(Constants.ROLE_ADMIN)
	@Audited(operation = EventOperation.SAVE)
	public ResponseEntity<Void> save(@RequestBody SystemPropertyDto dto) {
		service.save(dto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@DeleteMapping("/{key}")
	@PreAuthorize(Constants.ROLE_ADMIN)
	@Audited(operation = EventOperation.DELETE)
	public ResponseEntity<String> delete(@PathVariable("key") String key) {
		service.delete(key);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("/list")
	@PreAuthorize(Constants.ROLE_ADMIN)
	public @ResponseBody SystemPropertyListDto list(@RequestBody PagingDto dto) {
		return service.list(dto);
	}
}