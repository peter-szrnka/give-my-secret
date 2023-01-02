package io.github.gms.secure.service;

import io.github.gms.common.abstraction.AbstractCrudService;
import io.github.gms.secure.dto.ApiKeyDto;
import io.github.gms.secure.dto.ApiKeyListDto;
import io.github.gms.secure.dto.IdNamePairListDto;
import io.github.gms.secure.dto.SaveApiKeyRequestDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface ApiKeyService extends 
	AbstractCrudService<SaveApiKeyRequestDto, SaveEntityResponseDto, ApiKeyDto, ApiKeyListDto>, CountService {

	String getDecryptedValue(Long id);
	
	IdNamePairListDto getAllApiKeyNames();
}
