package io.github.gms.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum SystemStatus {
    NEED_SETUP(true,null), // Initial status
    NEED_ADMIN_USER(true, NEED_SETUP), // Admin user is not created
    NEED_AUTH_CONFIG(true, NEED_ADMIN_USER), // Authentication is not yet configured
    NEED_ORG_DATA(true, NEED_AUTH_CONFIG), // Organization data is not yet configured
    COMPLETE(true, NEED_ORG_DATA), // System is ready to use
    OK(false, COMPLETE); // System can be used

    private final boolean setupPhase;
    private final SystemStatus previousStatus;
}
