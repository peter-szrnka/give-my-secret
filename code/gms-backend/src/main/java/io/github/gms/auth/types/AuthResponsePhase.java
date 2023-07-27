package io.github.gms.auth.types;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public enum AuthResponsePhase {
    FAILED,
    MFA_REQUIRED,
    COMPLETED;
}
