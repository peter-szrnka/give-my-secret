package io.github.gms.functions.iprestriction;

import io.github.gms.common.model.IpRestrictionPattern;

import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface IpRestrictionValidator {

    boolean isIpAddressBlocked(List<IpRestrictionPattern> patterns);
}
