package io.github.gms.functions.apikey;

import io.github.gms.common.abstraction.AbstractClientController;
import io.github.gms.common.dto.IdNamePairListDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;
import io.github.gms.common.util.ConverterUtils;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/secure/apikey")
@AuditTarget(EventTarget.API_KEY)
public class ApiKeyController extends AbstractClientController<ApiKeyService> {

	public ApiKeyController(ApiKeyService service) {
		super(service);
	}

	@PostMapping
	@PreAuthorize(ROLE_USER)
	@Audited(operation = EventOperation.SAVE)
	public SaveEntityResponseDto save(@RequestBody SaveApiKeyRequestDto dto) {
		return service.save(dto);
	}
	
	@GetMapping(PATH_VARIABLE_ID)
	@PreAuthorize(ROLE_USER_OR_VIEWER)
	@Audited(operation = EventOperation.GET_BY_ID)
	public ApiKeyDto getById(@PathVariable(ID) Long id) {
		return service.getById(id);
	}
	
	@GetMapping(PATH_LIST)
	@PreAuthorize(ROLE_USER_OR_VIEWER)
	@Audited(operation = EventOperation.LIST)
	public ApiKeyListDto list(
			@RequestParam(DIRECTION) String direction,
			@RequestParam(PROPERTY) String property,
			@RequestParam(PAGE) int page,
			@RequestParam(SIZE) int size) {
		return service.list(ConverterUtils.createPageable(direction, property, page, size));
	}
	
	@GetMapping("/value/{id}")
	@PreAuthorize(ROLE_USER_OR_VIEWER)
	@Audited(operation = EventOperation.GET_VALUE)
	public String getValue(@PathVariable(ID) Long id) {
		return service.getDecryptedValue(id);
	}

	@GetMapping(PATH_LIST_NAMES)
	@PreAuthorize(ROLE_USER_OR_VIEWER)
	public IdNamePairListDto getAllApiKeyNames() {
		return service.getAllApiKeyNames();
	}
}
