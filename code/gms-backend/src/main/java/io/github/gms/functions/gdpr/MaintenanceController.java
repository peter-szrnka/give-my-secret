package io.github.gms.functions.gdpr;

import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;
import io.github.gms.functions.gdpr.model.BatchUserOperationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
@AuditTarget(EventTarget.MAINTENANCE)
@RequestMapping("/secure/maintenance")
public class MaintenanceController {

    private final UserDeletionService userDeletionService;

    @PostMapping("/request_user_deletion")
    @Audited(operation = EventOperation.REQUEST_USER_DELETION)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TECHNICAL')")
    public ResponseEntity<Void> requestUserDeletion(@RequestBody BatchUserOperationDto dto) {
        userDeletionService.requestUserDeletion(dto);
        return ResponseEntity.status(OK).build();
    }
}