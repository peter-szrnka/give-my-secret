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
    NEED_SETUP(null), // Initial status
    NEED_ADMIN_USER(NEED_SETUP), // Admin user is not created
    NEED_AUTH_CONFIG(NEED_ADMIN_USER), // Authentication is not yet configured
    NEED_ORG_DATA(NEED_AUTH_CONFIG), // Organization data is not yet configured
    COMPLETE(NEED_ORG_DATA), // System is ready to use
    OK(COMPLETE); // System can be used

    private final SystemStatus previousStatus;
}
