package io.github.gms.functions.secret;

import io.github.gms.common.abstraction.AbstractClientController;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;
import io.github.gms.common.util.ConverterUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static io.github.gms.common.util.Constants.ID;
import static io.github.gms.common.util.Constants.PATH_LIST;
import static io.github.gms.common.util.Constants.PATH_VARIABLE_ID;
import static io.github.gms.common.util.Constants.ROLE_USER;
import static io.github.gms.common.util.Constants.ROLE_USER_OR_VIEWER;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/secure/secret")
@AuditTarget(EventTarget.SECRET)
public class SecretController extends AbstractClientController<SecretService> {

	private final SecretRotationService secretRotationService;

	public SecretController(SecretService service, SecretRotationService secretRotationService) {
		super(service);
		this.secretRotationService = secretRotationService;
	}

	@PostMapping
	@PreAuthorize(ROLE_USER)
	@Audited(operation = EventOperation.SAVE)
	public SaveEntityResponseDto save(@RequestBody SaveSecretRequestDto dto) {
		return service.save(dto);
	}

	@GetMapping(PATH_VARIABLE_ID)
	@PreAuthorize(ROLE_USER_OR_VIEWER)
	public SecretDto getById(@PathVariable(ID) Long id) {
		return service.getById(id);
	}

	@GetMapping(PATH_LIST)
	@PreAuthorize(ROLE_USER_OR_VIEWER)
	public SecretListDto list(
			@RequestParam("direction") String direction,
			@RequestParam("property") String property,
			@RequestParam("size") int page,
			@RequestParam("size") int size) {
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
}
