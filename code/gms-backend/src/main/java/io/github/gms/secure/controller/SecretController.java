package io.github.gms.secure.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.gms.common.abstraction.AbstractClientController;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;
import io.github.gms.common.util.Constants;
import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.dto.SaveSecretRequestDto;
import io.github.gms.secure.dto.SecretDto;
import io.github.gms.secure.dto.SecretListDto;
import io.github.gms.secure.service.SecretRotationService;
import io.github.gms.secure.service.SecretService;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/secure/secret")
@AuditTarget(EventTarget.SECRET)
public class SecretController extends AbstractClientController<SecretService> {
	
	@Autowired
	private SecretRotationService secretRotationService;

	@PostMapping
	@PreAuthorize("hasRole('ROLE_USER')")
	@Audited(operation = EventOperation.SAVE)
	public @ResponseBody SaveEntityResponseDto save(@RequestBody SaveSecretRequestDto dto) {
		return service.save(dto);
	}

	@GetMapping("/{id}")
	@PreAuthorize(Constants.ROLE_USER_OR_VIEWER)
	public @ResponseBody SecretDto getById(@PathVariable("id") Long id) {
		return service.getById(id);
	}

	@PostMapping("/list")
	@PreAuthorize(Constants.ROLE_USER_OR_VIEWER)
	public @ResponseBody SecretListDto list(@RequestBody PagingDto dto) {
		return service.list(dto);
	}
	
	@GetMapping("/value/{id}")
	@PreAuthorize(Constants.ROLE_USER_OR_VIEWER)
	@Audited(operation = EventOperation.GET_VALUE)
	public @ResponseBody ResponseEntity<String> getValue(@PathVariable("id") Long id) {
		return new ResponseEntity<>(service.getSecretValue(id), HttpStatus.OK);
	}

	@GetMapping("/count")
	@PreAuthorize(Constants.ROLE_USER_OR_VIEWER)
	public @ResponseBody LongValueDto userCount() {
		return service.count();
	}
	
	@PostMapping("/rotate/{id}")
	@PreAuthorize(Constants.ROLE_USER_OR_VIEWER)
	@Audited(operation = EventOperation.ROTATE_SECRET_MANUALLY)
	public @ResponseBody ResponseEntity<String> rotateSecret(@PathVariable("id") Long id) {
		secretRotationService.rotateSecretById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
