package io.github.gms.secure.controller;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.github.gms.common.abstraction.AbstractClientController;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;
import io.github.gms.common.util.Constants;
import io.github.gms.secure.dto.GetSecureValueDto;
import io.github.gms.secure.dto.IdNamePairListDto;
import io.github.gms.secure.dto.KeystoreDto;
import io.github.gms.secure.dto.KeystoreListDto;
import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.service.KeystoreService;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/secure/keystore")
@AuditTarget(EventTarget.KEYSTORE)
public class KeystoreController extends AbstractClientController<KeystoreService> {
	
	public static final String MULTIPART_MODEL = "model";
	public static final String MULTIPART_FILE = "file";

	@PostMapping(consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE,
			MediaType.MULTIPART_MIXED_VALUE
	}, produces = {
			MediaType.APPLICATION_JSON_VALUE
	})
	@PreAuthorize("hasRole('ROLE_USER')")
	@Audited(operation = EventOperation.SAVE)
	public @ResponseBody SaveEntityResponseDto save(@ModelAttribute(name = MULTIPART_MODEL) String model, @RequestPart(name = MULTIPART_FILE, required = false) MultipartFile file) {
		return service.save(model, file);
	}
	
	@GetMapping("/{id}")
	@PreAuthorize(Constants.ROLE_USER_OR_VIEWER)
	@Audited(operation = EventOperation.GET_BY_ID)
	public @ResponseBody KeystoreDto getById(@PathVariable("id") Long id) {
		return service.getById(id);
	}
	
	@PostMapping("/list")
	@PreAuthorize(Constants.ROLE_USER_OR_VIEWER)
	public @ResponseBody KeystoreListDto list(@RequestBody PagingDto dto) {
		return service.list(dto);
	}
	
	@PostMapping("/value")
	@PreAuthorize(Constants.ROLE_USER_OR_VIEWER)
	@Audited(operation = EventOperation.GET_VALUE)
	public @ResponseBody String getValue(@RequestBody GetSecureValueDto dto) {
		return service.getValue(dto);
	}
	
	@GetMapping("/count")
	@PreAuthorize(Constants.ROLE_USER_OR_VIEWER)
	public @ResponseBody LongValueDto userCount() {
		return service.count();
	}
	
	@GetMapping("/list_names")
	@PreAuthorize(Constants.ROLE_USER_OR_VIEWER)
	public @ResponseBody IdNamePairListDto getAllKeystoreNames() {
		return service.getAllKeystoreNames();
	}
}
