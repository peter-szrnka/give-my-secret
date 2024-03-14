package io.github.gms.functions.iprestriction;

import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface IpRestrictionService {

    void updateIpRestrictionsForSecret(Long secretId, List<IpRestrictionDto> ipRestrictions);

    List<IpRestrictionDto> getAllBySecretId(Long secretId);

    void checkIpRestrictionsBySecret(Long secretId);

    void checkGlobalIpRestrictions();
}
