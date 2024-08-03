package io.github.gms.common.controller;

import io.github.gms.common.dto.ErrorCodeDto;
import io.github.gms.common.dto.ErrorCodeListDto;
import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.common.types.ErrorCode;
import io.github.gms.functions.system.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.stream.Stream;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class SystemController {

	private final SystemService systemService;

	@GetMapping("system/status")
	public SystemStatusDto status() {
		return systemService.getSystemStatus();
	}

	@GetMapping("error_codes")
	public ErrorCodeListDto getErrorCodes() {
		return new ErrorCodeListDto(Stream.of(ErrorCode.values())
				.sorted(Comparator.comparing(ErrorCode::getCode))
				.map(errorCode -> new ErrorCodeDto(errorCode.getCode(), errorCode.getDescription()))
				.toList());
	}
}
