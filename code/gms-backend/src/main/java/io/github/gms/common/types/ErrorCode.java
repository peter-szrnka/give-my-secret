package io.github.gms.common.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    GMS_000("GMS-000", "Default error code"),
    GMS_001("GMS-001", "Unexpected IO error"),
    GMS_002("GMS-002", "Entity does not exist"),
    // User
    GMS_003("GMS-003", "User already exists"),
    GMS_004("GMS-004", "Old credential is not valid"),
    GMS_005("GMS-005", "New credential is not valid"),
    // Keystore
    GMS_006("GMS-006", "Keystore alias is not available"),
    GMS_007("GMS-007", "Invalid keystore"),
    GMS_008("GMS-008", "Invalid keystore alias"),
    GMS_009("GMS-009", "Keystore file name must be unique"),
    GMS_010("GMS-010", "At least one keystore alias must be defined"),
    GMS_011("GMS-011", "User cannot upload a keystore along with a generated keystore, only one can be selected"),
    GMS_012("GMS-012", "Keystore file must be provided!"),
    GMS_013("GMS-013", "Keystore name must be unique!"),
    GMS_014("GMS-014", "Keystore file does not exist"),
    GMS_015("GMS-015", "Please provide an active keystore"),
    // API keys
    GMS_016("GMS-016", "Wrong API key"),
    GMS_017("GMS-017", "It is not allowed to use this API key for this secret"),
    GMS_018("GMS-018", "API key name must be unique"),
    GMS_019("GMS-019", "API key value must be unique"),
    // Secrets
    GMS_020("GMS-020", "Secret ID name must be unique"),
    GMS_021("GMS-021", "Username password pair is invalid in secret"),
    GMS_022("GMS-022", "Secret is not available"),
    GMS_023("GMS-023", "It is not allowed to get this secret from your IP address"),
    GMS_024("GMS-024", "Only global IP restrictions allowed to save with this service"),
    GMS_025("GMS-025", "the given resource is not a global IP restriction"),
    // System property
    GMS_026("GMS-026", "Unknown system property"),
    ;

    private final String code;
    private final String description;
}
