package io.github.gms.common.controller;

import io.github.gms.common.abstraction.GmsController;
import io.github.gms.common.dto.*;
import io.github.gms.common.types.ErrorCode;
import io.github.gms.common.types.SkipSecurityTestCheck;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.user.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/info")
@SkipSecurityTestCheck
@RequiredArgsConstructor
public class InformationController implements GmsController {
    
    private final UserInfoService userInfoService;
    private final SystemService systemService;

    @GetMapping("/vm_options")
    @PostAuthorize("@vmOptionsPostAuthorize.canAccess()")
    public @ResponseBody List<VmOptionDto> getVmOptions() {
        return systemService.getVmOptions();
    }

	@GetMapping("/me")
    public UserInfoDto getUserInfo(HttpServletRequest request) {
        return userInfoService.getUserInfo(request);
    }

    @GetMapping("/status")
    public SystemStatusDto status() {
        return systemService.getSystemStatus();
    }

    @GetMapping("/error_codes")
    public ErrorCodeListDto getErrorCodes() {
        return new ErrorCodeListDto(Stream.of(ErrorCode.values())
                .sorted(Comparator.comparing(ErrorCode::getCode))
                .map(errorCode -> new ErrorCodeDto(errorCode.getCode()))
                .toList());
    }
}