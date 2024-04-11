package io.github.gms.functions.gdpr;

import io.github.gms.functions.gdpr.model.BatchUserOperationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/gdpr")
@RequiredArgsConstructor
public class GdprController {

    private final UserDeletionService userDeletionService;

    @GetMapping("/request_user_deletion")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TECHNICAL')")
    public ResponseEntity<Void> requestUserDeletion(@RequestBody BatchUserOperationDto dto) {
        userDeletionService.requestUserDeletion(dto);
        return ResponseEntity.status(OK).build();
    }
}