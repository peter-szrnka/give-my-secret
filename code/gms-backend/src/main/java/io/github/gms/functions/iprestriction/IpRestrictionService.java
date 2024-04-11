package io.github.gms.functions.iprestriction;

import io.github.gms.common.abstraction.AbstractCrudService;
import io.github.gms.common.abstraction.BatchDeletionService;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.model.IpRestrictionPatterns;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface IpRestrictionService extends
        AbstractCrudService<IpRestrictionDto, SaveEntityResponseDto, IpRestrictionDto, IpRestrictionListDto>,
        BatchDeletionService {

    SaveEntityResponseDto save(IpRestrictionDto dto);

    IpRestrictionListDto list(Pageable pageable);

    IpRestrictionDto getById(Long id);

    void delete(Long id);

    void updateIpRestrictionsForSecret(Long secretId, List<IpRestrictionDto> ipRestrictions);

    List<IpRestrictionDto> getAllBySecretId(Long secretId) ;

    IpRestrictionPatterns checkIpRestrictionsBySecret(Long secretId);

    IpRestrictionPatterns checkGlobalIpRestrictions();

    void toggleStatus(Long id, boolean enabled);

    void batchDeleteByUserIds(Set<Long> userIds);
}
