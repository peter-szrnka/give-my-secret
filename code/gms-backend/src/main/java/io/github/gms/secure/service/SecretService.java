package io.github.gms.secure.service;

import io.github.gms.common.abstraction.AbstractCrudService;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.dto.SaveSecretRequestDto;
import io.github.gms.secure.dto.SecretDto;
import io.github.gms.secure.dto.SecretListDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface SecretService
	extends AbstractCrudService<SaveSecretRequestDto, SaveEntityResponseDto, SecretDto, SecretListDto>, CountService {
	
	String getSecretValue(Long id);
}
