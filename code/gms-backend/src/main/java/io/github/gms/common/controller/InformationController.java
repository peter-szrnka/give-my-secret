package io.github.gms.common.controller;

import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.functions.user.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/info")
@RequiredArgsConstructor
public class InformationController {
    
    private final UserInfoService userInfoService;

	@GetMapping("/me")
    public UserInfoDto getUserInfo(HttpServletRequest request) {
        return userInfoService.getUserInfo(request);
    }
}