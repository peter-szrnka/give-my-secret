package io.github.gms.common.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    GMS_000("GMS-000"),
    GMS_001("GMS-001"),
    GMS_002("GMS-002"),
    // User
    GMS_003("GMS-003"),
    GMS_004("GMS-004"),
    GMS_005("GMS-005"),
    // Keystore
    GMS_006("GMS-006"),
    GMS_007("GMS-007"),
    GMS_008("GMS-008"),
    GMS_009("GMS-009"),
    GMS_010("GMS-010"),
    GMS_011("GMS-011"),
    GMS_012("GMS-012"),
    GMS_013("GMS-013"),
    GMS_014("GMS-014"),
    GMS_015("GMS-015"),
    // API keys
    GMS_016("GMS-016"),
    GMS_017("GMS-017"),
    GMS_018("GMS-018"),
    GMS_019("GMS-019"),
    // Secrets
    GMS_020("GMS-020"),
    GMS_021("GMS-021"),
    GMS_022("GMS-022"),
    GMS_023("GMS-023"),
    GMS_024("GMS-024"),
    GMS_025("GMS-025"),
    // System property
    GMS_026("GMS-026"),
    GMS_027("GMS-027"),
    ;

    private final String code;
}
