package io.github.gms.secure.controller;

import io.github.gms.secure.dto.HomeDataResponseDto;
import io.github.gms.secure.service.HomeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static io.github.gms.common.util.Constants.ALL_ROLE;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/secure/home")
public class HomeController {

    private final HomeService service;

    public HomeController(HomeService service) {
        this.service = service;
    }

    @GetMapping("/")
    @PreAuthorize(ALL_ROLE)
    public @ResponseBody HomeDataResponseDto getHomeData() {
        return service.getHomeData();
    }
}
