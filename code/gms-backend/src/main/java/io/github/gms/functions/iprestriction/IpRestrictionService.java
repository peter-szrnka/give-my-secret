package io.github.gms.functions.iprestriction;

import io.github.gms.common.dto.PagingDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.model.IpRestrictionPattern;

import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface IpRestrictionService {

    SaveEntityResponseDto save(IpRestrictionDto dto);

    IpRestrictionListDto list(PagingDto dto);

    IpRestrictionDto getById(Long id);

    void delete(Long id);

    void updateIpRestrictionsForSecret(Long secretId, List<IpRestrictionDto> ipRestrictions);

    List<IpRestrictionDto> getAllBySecretId(Long secretId);

    List<IpRestrictionPattern> checkIpRestrictionsBySecret(Long secretId);

    List<IpRestrictionPattern> checkGlobalIpRestrictions();

    void toggleStatus(Long id, boolean enabled);
}
