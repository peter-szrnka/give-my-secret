package io.github.gms.common.controller;

import io.github.gms.common.abstraction.GmsController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
public class HealthcheckController implements GmsController {

	@GetMapping("/healthcheck")
	public ResponseEntity<Void> healthcheck() {
		return ResponseEntity.ok().build();
	}
}
