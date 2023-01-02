package io.github.gms.secure.controller;

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
import io.github.gms.secure.dto.ApiKeyDto;
import io.github.gms.secure.dto.ApiKeyListDto;
import io.github.gms.secure.dto.IdNamePairListDto;
import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveApiKeyRequestDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.service.ApiKeyService;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/secure/apikey")
@AuditTarget(EventTarget.API_KEY)
public class ApiKeyController extends AbstractClientController<ApiKeyService> {

	@PostMapping
	@PreAuthorize("hasRole('ROLE_USER')")
	@Audited(operation = EventOperation.SAVE)
	public @ResponseBody SaveEntityResponseDto save(@RequestBody SaveApiKeyRequestDto dto) {
		return service.save(dto);
	}
	
	@GetMapping("/{id}")
	@PreAuthorize(Constants.ROLE_USER_OR_VIEWER)
	@Audited(operation = EventOperation.GET_BY_ID)
	public @ResponseBody ApiKeyDto getById(@PathVariable("id") Long id) {
		return service.getById(id);
	}
	
	@PostMapping("/list")
	@PreAuthorize(Constants.ROLE_USER_OR_VIEWER)
	public @ResponseBody ApiKeyListDto list(@RequestBody PagingDto dto) {
		return service.list(dto);
	}
	
	@GetMapping("/value/{id}")
	@PreAuthorize(Constants.ROLE_USER_OR_VIEWER)
	@Audited(operation = EventOperation.GET_VALUE)
	public String getValue(@PathVariable("id") Long id) {
		return service.getDecryptedValue(id);
	}

	@GetMapping("/count")
	@PreAuthorize(Constants.ROLE_USER_OR_VIEWER)
	public @ResponseBody LongValueDto userCount() {
		return service.count();
	}
	
	@GetMapping("/list_names")
	@PreAuthorize(Constants.ROLE_USER_OR_VIEWER)
	public @ResponseBody IdNamePairListDto getAllApiKeyNames() {
		return service.getAllApiKeyNames();
	}
}
