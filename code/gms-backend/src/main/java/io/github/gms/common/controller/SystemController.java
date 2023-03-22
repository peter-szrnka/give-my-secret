package io.github.gms.common.controller;

import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.secure.service.SystemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/system")
public class SystemController {

	private final SystemService systemService;

	public SystemController(SystemService systemService) {
		this.systemService = systemService;
	}

	@ResponseBody
	@GetMapping("/status")
	public SystemStatusDto status(HttpServletRequest request) {
		return systemService.getSystemStatus();
	}
}
