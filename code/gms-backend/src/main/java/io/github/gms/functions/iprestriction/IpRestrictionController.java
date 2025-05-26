package io.github.gms.functions.iprestriction;

import io.github.gms.common.abstraction.AbstractAdminController;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;
import io.github.gms.common.util.ConverterUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static io.github.gms.common.util.Constants.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/secure/ip_restriction")
@AuditTarget(EventTarget.IP_RESTRICTION)
public class IpRestrictionController extends AbstractAdminController<IpRestrictionService> {
    protected IpRestrictionController(IpRestrictionService service) {
        super(service);
    }

    @PostMapping
    @PreAuthorize(ROLE_ADMIN)
    @Audited(operation = EventOperation.SAVE)
    public SaveEntityResponseDto save(@RequestBody IpRestrictionDto dto) {
        return service.save(dto);
    }

    @GetMapping(PATH_VARIABLE_ID)
    @PreAuthorize(ROLE_ADMIN)
    @Audited(operation = EventOperation.GET_BY_ID)
    public IpRestrictionDto getById(@PathVariable(ID) Long id) {
        return service.getById(id);
    }

    @GetMapping(PATH_LIST)
    @PreAuthorize(ROLE_ADMIN)
    public IpRestrictionListDto list(
            @RequestParam(DIRECTION) String direction,
            @RequestParam(PROPERTY) String property,
            @RequestParam(PAGE) int page,
            @RequestParam(SIZE) int size) {
        return service.list(ConverterUtils.createPageable(direction, property, page, size));
    }
}
