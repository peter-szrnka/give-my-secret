package io.github.gms.secure.controller;

import io.github.gms.common.abstraction.AbstractController;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;
import io.github.gms.secure.dto.AnnouncementDto;
import io.github.gms.secure.dto.AnnouncementListDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveAnnouncementDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.service.AnnouncementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static io.github.gms.common.util.Constants.ALL_ROLE;
import static io.github.gms.common.util.Constants.ROLE_ADMIN;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/secure/announcement")
@AuditTarget(EventTarget.ANNOUNCEMENT)
public class AnnouncementController extends AbstractController<AnnouncementService> {

	@PostMapping
	@PreAuthorize(ROLE_ADMIN)
	@Audited(operation = EventOperation.SAVE)
	public @ResponseBody SaveEntityResponseDto save(@RequestBody SaveAnnouncementDto dto) {
		return service.save(dto);
	}
	
	@GetMapping("/{id}")
	@PreAuthorize(ALL_ROLE)
	public @ResponseBody AnnouncementDto getById(@PathVariable("id") Long id) {
		return service.getById(id);
	}
	
	@PostMapping("/list")
	@PreAuthorize(ALL_ROLE)
	public @ResponseBody AnnouncementListDto list(@RequestBody PagingDto dto) {
		return service.list(dto);
	}
	
	@DeleteMapping("/{id}")
	@PreAuthorize(ROLE_ADMIN)
	@Audited(operation = EventOperation.DELETE)
	public @ResponseBody ResponseEntity<String> delete(@PathVariable("id") Long id) {
		service.delete(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
