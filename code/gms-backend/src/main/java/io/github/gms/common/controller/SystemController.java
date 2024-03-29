package io.github.gms.common.controller;

import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.functions.system.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/system")
public class SystemController {

	private final SystemService systemService;

	@GetMapping("/status")
	public SystemStatusDto status() {
		return systemService.getSystemStatus();
	}
}
