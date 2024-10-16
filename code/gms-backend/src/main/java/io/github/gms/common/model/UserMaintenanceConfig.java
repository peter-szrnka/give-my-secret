package io.github.gms.common.model;

import io.github.gms.common.enums.EntityStatus;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public record UserMaintenanceConfig(String scope, EntityStatus newStatus) {
}
