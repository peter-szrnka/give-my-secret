package io.github.gms.common.abstraction;

import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.types.Audited;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static io.github.gms.common.util.Constants.ID;
import static io.github.gms.common.util.Constants.PATH_ENABLED;
import static io.github.gms.common.util.Constants.PATH_VARIABLE_ID;
import static io.github.gms.common.util.Constants.ROLE_USER;

/**
 * @author Peter Szrnka
 * @since 1.0
 * 
 * @param <T> An extended GmsClientService
 */
public abstract class AbstractClientController<T extends GmsClientService> extends AbstractController<T> {

	protected AbstractClientController(T service) {
		super(service);
	}

	@DeleteMapping(PATH_VARIABLE_ID)
	@PreAuthorize(ROLE_USER)
	@Audited(operation = EventOperation.DELETE)
	public ResponseEntity<String> delete(@PathVariable(ID) Long id) {
		service.delete(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(PATH_VARIABLE_ID)
	@PreAuthorize(ROLE_USER)
	@Audited(operation = EventOperation.TOGGLE_STATUS)
	public ResponseEntity<String> toggle(@PathVariable(ID) Long id, @RequestParam(PATH_ENABLED) boolean enabled) {
		service.toggleStatus(id, enabled);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
