package io.github.gms.functions.iprestriction;

import io.github.gms.common.abstraction.AbstractAdminController;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;
import io.github.gms.common.util.ConverterUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static io.github.gms.common.util.Constants.ID;
import static io.github.gms.common.util.Constants.PATH_LIST;
import static io.github.gms.common.util.Constants.PATH_VARIABLE_ID;
import static io.github.gms.common.util.Constants.ROLE_ADMIN;

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
            @RequestParam("direction") String direction,
            @RequestParam("property") String property,
            @RequestParam("size") int page,
            @RequestParam("size") int size) {
        return service.list(ConverterUtils.createPageable(direction, property, page, size));
    }
}
