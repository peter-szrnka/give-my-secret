package io.github.gms.functions.home;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.github.gms.common.util.Constants.ALL_ROLE;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/secure/home")
public class HomeController {

    private final HomeService service;

    @GetMapping("/")
    @PreAuthorize(ALL_ROLE)
    public HomeDataResponseDto getHomeData() {
        return service.getHomeData();
    }
}
