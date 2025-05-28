package io.github.gms.auth;

import io.github.gms.functions.system.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static io.github.gms.common.enums.SystemStatus.NEED_SETUP;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RequiredArgsConstructor
@Component("vmOptionsPostAuthorize")
public class VmOptionsPostAuthorize {

    private final SystemService service;

    public boolean canAccess() {
        if (NEED_SETUP.name().equals(service.getSystemStatus().getStatus())) {
            return true;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(granted -> granted.getAuthority().equals("ROLE_ADMIN")
        );
    }
}