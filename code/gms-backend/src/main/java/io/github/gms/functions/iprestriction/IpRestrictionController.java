package io.github.gms.functions.iprestriction;

import io.github.gms.common.dto.PagingDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequiredArgsConstructor
@RequestMapping("/secure/ip_restriction")
@AuditTarget(EventTarget.IP_RESTRICTION)
public class IpRestrictionController {

    private final IpRestrictionService service;

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

    @PostMapping(PATH_LIST)
    @PreAuthorize(ROLE_ADMIN)
    public IpRestrictionListDto list(@RequestBody PagingDto dto) {
        return service.list(dto);
    }

    @DeleteMapping(PATH_VARIABLE_ID)
    @PreAuthorize(ROLE_ADMIN)
    @Audited(operation = EventOperation.DELETE)
    public ResponseEntity<String> delete(@PathVariable(ID) Long id) {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
