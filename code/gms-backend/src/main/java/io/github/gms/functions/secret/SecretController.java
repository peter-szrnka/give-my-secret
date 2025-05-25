package io.github.gms.functions.secret;

import io.github.gms.common.abstraction.AbstractClientController;
import io.github.gms.common.dto.BooleanValueDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.functions.secret.dto.SaveSecretRequestDto;
import io.github.gms.functions.secret.dto.SecretDto;
import io.github.gms.functions.secret.dto.SecretListDto;
import io.github.gms.functions.secret.dto.SecretValueDto;
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
@RequestMapping("/secure/secret")
@AuditTarget(EventTarget.SECRET)
public class SecretController extends AbstractClientController<SecretService> {

	private final SecretRotationService secretRotationService;
	private final SecretLengthValidatorService secretLengthValidatorService;

	public SecretController(SecretService service, SecretRotationService secretRotationService, SecretLengthValidatorService secretLengthValidatorService) {
		super(service);
		this.secretRotationService = secretRotationService;
		this.secretLengthValidatorService = secretLengthValidatorService;
	}

	@PostMapping
	@PreAuthorize(ROLE_USER)
	@Audited(operation = EventOperation.SAVE)
	public SaveEntityResponseDto save(@RequestBody SaveSecretRequestDto dto) {
		return service.save(dto);
	}

	@GetMapping(PATH_VARIABLE_ID)
	@PreAuthorize(ROLE_USER_OR_VIEWER)
	@Audited(operation = EventOperation.GET_BY_ID)
	public SecretDto getById(@PathVariable(ID) Long id) {
		return service.getById(id);
	}

	@GetMapping(PATH_LIST)
	@PreAuthorize(ROLE_USER_OR_VIEWER)
	@Audited(operation = EventOperation.LIST)
	public SecretListDto list(
			@RequestParam(DIRECTION) String direction,
			@RequestParam(PROPERTY) String property,
			@RequestParam(PAGE) int page,
			@RequestParam(SIZE) int size) {
		return service.list(ConverterUtils.createPageable(direction, property, page, size));
	}
	
	@GetMapping("/value/{id}")
	@PreAuthorize(ROLE_USER_OR_VIEWER)
	@Audited(operation = EventOperation.GET_VALUE)
	public ResponseEntity<String> getValue(@PathVariable(ID) Long id) {
		return new ResponseEntity<>(service.getSecretValue(id), HttpStatus.OK);
	}
	
	@PostMapping("/rotate/{id}")
	@PreAuthorize(ROLE_USER_OR_VIEWER)
	@Audited(operation = EventOperation.ROTATE_SECRET_MANUALLY)
	public ResponseEntity<String> rotateSecret(@PathVariable(ID) Long id) {
		secretRotationService.rotateSecretById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/validate_value_length")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<BooleanValueDto> validateValueLength(@RequestBody SecretValueDto dto) {
		return new ResponseEntity<>(secretLengthValidatorService.validateValueLength(dto), HttpStatus.OK);
	}
}
