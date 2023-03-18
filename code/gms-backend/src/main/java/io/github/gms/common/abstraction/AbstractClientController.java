package io.github.gms.common.abstraction;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.types.Audited;
import io.github.gms.common.util.Constants;

/**
 * @author Peter Szrnka
 * @since 1.0
 * 
 * @param <T> An extended GmsClientService
 */
public abstract class AbstractClientController<T extends GmsClientService> extends AbstractController<T> {

	@DeleteMapping("/{id}")
	@PreAuthorize(Constants.ROLE_USER)
	@Audited(operation = EventOperation.DELETE)
	public ResponseEntity<String> delete(@PathVariable("id") Long id) {
		service.delete(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/{id}")
	@PreAuthorize(Constants.ROLE_USER)
	@Audited(operation = EventOperation.TOGGLE_STATUS)
	public ResponseEntity<String> toggle(@PathVariable("id") Long id, @RequestParam("enabled") boolean enabled) {
		service.toggleStatus(id, enabled);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
