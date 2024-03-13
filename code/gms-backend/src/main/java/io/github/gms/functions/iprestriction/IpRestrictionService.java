package io.github.gms.functions.iprestriction;

import io.github.gms.common.dto.PagingDto;
import io.github.gms.common.dto.SaveEntityResponseDto;

import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface IpRestrictionService {

    SaveEntityResponseDto save(SaveIpRestrictionDto dto);

    IpRestrictionDto getById(Long id);

    IpRestrictionListDto list(PagingDto dto);

    void delete(Long id);
    List<IpRestrictionPattern> getIpRestrictionsBySecret(Long userId, Long secretId);
}
