package io.github.gms.functions.apikey;

import io.github.gms.common.abstraction.AbstractCrudService;
import io.github.gms.common.abstraction.BatchDeletionService;
import io.github.gms.common.dto.IdNamePairListDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.service.CountService;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface ApiKeyService extends 
	AbstractCrudService<SaveApiKeyRequestDto, SaveEntityResponseDto, ApiKeyDto, ApiKeyListDto>, CountService,
		BatchDeletionService {

	String getDecryptedValue(Long id);
	
	IdNamePairListDto getAllApiKeyNames();
}
