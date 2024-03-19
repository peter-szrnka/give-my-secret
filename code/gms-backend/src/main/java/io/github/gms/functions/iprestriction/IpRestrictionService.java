package io.github.gms.functions.iprestriction;

import io.github.gms.common.abstraction.AbstractCrudService;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.model.IpRestrictionPatterns;

import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface IpRestrictionService extends
        AbstractCrudService<IpRestrictionDto, SaveEntityResponseDto, IpRestrictionDto, IpRestrictionListDto> {

    void updateIpRestrictionsForSecret(Long secretId, List<IpRestrictionDto> ipRestrictions);

    List<IpRestrictionDto> getAllBySecretId(Long secretId);

    IpRestrictionPatterns checkIpRestrictionsBySecret(Long secretId);

    IpRestrictionPatterns checkGlobalIpRestrictions();
}
