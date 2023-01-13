package io.github.gms.common.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.secure.service.SystemService;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/system")
public class SystemController {
	
	@Autowired
	private SystemService systemService;

	@ResponseBody
	@GetMapping("/status")
	public SystemStatusDto status(HttpServletRequest request) {
		return systemService.getSystemStatus();
	}
}
