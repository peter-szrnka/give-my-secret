package io.github.gms.functions.maintenance.job;

import io.github.gms.common.abstraction.GmsController;
import io.github.gms.common.util.ConverterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static io.github.gms.common.util.Constants.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/secure/job")
public class JobMaintenanceController implements GmsController {

    private final JobMaintenanceService service;

    @GetMapping(PATH_LIST)
    @PreAuthorize(ROLE_ADMIN)
    public JobListDto list(
            @RequestParam(DIRECTION) String direction,
            @RequestParam(PROPERTY) String property,
            @RequestParam(PAGE) int page,
            @RequestParam(SIZE) int size) {
        return service.list(ConverterUtils.createPageable(direction, property, page, size));
    }
}
