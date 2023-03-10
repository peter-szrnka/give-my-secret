package io.github.gms.secure.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.gms.common.abstraction.AbstractController;
import io.github.gms.secure.dto.EventListDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.service.EventService;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/secure/event")
public class EventController extends AbstractController<EventService> {

	@PostMapping("/list")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_VIEWER')")
	public @ResponseBody EventListDto list(@RequestBody PagingDto dto) {
		return service.list(dto);
	}
}
