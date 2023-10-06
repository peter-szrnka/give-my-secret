package io.github.gms.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.gms.secure.dto.UserInfoDto;
import io.github.gms.secure.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/info")
public class InformationController {
    
    private final UserService userService;

    public InformationController(UserService userService) {
        this.userService = userService;
    }

	@GetMapping("/me")
    public @ResponseBody UserInfoDto getUserInfo(HttpServletRequest request) {
        return userService.getUserInfo(request);
    }
}