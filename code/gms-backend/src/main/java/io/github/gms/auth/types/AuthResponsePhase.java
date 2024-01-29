package io.github.gms.auth.types;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public enum AuthResponsePhase {
    BLOCKED,
    FAILED,
    MFA_REQUIRED,
    COMPLETED;
}
