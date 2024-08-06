package io.github.gms.functions.apikey;

import io.github.gms.common.abstraction.AbstractCrudService;
import io.github.gms.common.abstraction.BatchDeletionService;
import io.github.gms.common.dto.IdNamePairListDto;
import io.github.gms.common.dto.LongValueDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.service.CountService;
import org.springframework.data.domain.Pageable;

import java.util.Set;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface ApiKeyService extends AbstractCrudService<SaveApiKeyRequestDto, SaveEntityResponseDto, ApiKeyDto, ApiKeyListDto>,
		CountService, BatchDeletionService {

	SaveEntityResponseDto save(SaveApiKeyRequestDto dto);

	ApiKeyDto getById(Long id);

	ApiKeyListDto list(Pageable pageable);

	void delete(Long id);

	void toggleStatus(Long id, boolean enabled);

	String getDecryptedValue(Long id);

	LongValueDto count();

	IdNamePairListDto getAllApiKeyNames();

	void batchDeleteByUserIds(Set<Long> userIds);
}
