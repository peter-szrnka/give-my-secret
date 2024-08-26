package io.github.gms.common.controller;

import io.github.gms.common.abstraction.GmsController;
import io.github.gms.common.dto.ErrorCodeDto;
import io.github.gms.common.dto.ErrorCodeListDto;
import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.common.types.ErrorCode;
import io.github.gms.common.types.SkipTestAnnotationCheck;
import io.github.gms.functions.system.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.stream.Stream;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/")
@SkipTestAnnotationCheck
@RequiredArgsConstructor
public class SystemController implements GmsController {

	private final SystemService systemService;

	@GetMapping("system/status")
	public @ResponseBody SystemStatusDto status() {
		return systemService.getSystemStatus();
	}

	@GetMapping("error_codes")
	public @ResponseBody ErrorCodeListDto getErrorCodes() {
		return new ErrorCodeListDto(Stream.of(ErrorCode.values())
				.sorted(Comparator.comparing(ErrorCode::getCode))
				.map(errorCode -> new ErrorCodeDto(errorCode.getCode(), errorCode.getDescription()))
				.toList());
	}
}
