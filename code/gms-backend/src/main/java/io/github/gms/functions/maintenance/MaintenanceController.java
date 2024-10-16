package io.github.gms.functions.maintenance;

import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;
import io.github.gms.functions.maintenance.model.BatchUserOperationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.github.gms.common.util.Constants.ROLE_ADMIN_OR_TECHNICAL;
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

    private final UserAnonymizationService userAnonymizationService;
    private final UserDeletionService userDeletionService;

    @PostMapping("/request_user_anonymization")
    @Audited(operation = EventOperation.REQUEST_USER_ANONYMIZATION)
    @PreAuthorize(ROLE_ADMIN_OR_TECHNICAL)
    public ResponseEntity<Void> requestUserAnonymization(@RequestBody BatchUserOperationDto dto) {
        userAnonymizationService.requestProcess(dto);
        return ResponseEntity.status(OK).build();
    }

    @PostMapping("/request_user_deletion")
    @Audited(operation = EventOperation.REQUEST_USER_DELETION)
    @PreAuthorize(ROLE_ADMIN_OR_TECHNICAL)
    public ResponseEntity<Void> requestUserDeletion(@RequestBody BatchUserOperationDto dto) {
        userDeletionService.requestProcess(dto);
        return ResponseEntity.status(OK).build();
    }
}