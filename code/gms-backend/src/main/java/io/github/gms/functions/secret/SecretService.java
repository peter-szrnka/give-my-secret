package io.github.gms.functions.secret;

import io.github.gms.common.abstraction.AbstractCrudService;
import io.github.gms.common.abstraction.BatchDeletionService;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.service.CountService;
import io.github.gms.functions.secret.dto.SaveSecretRequestDto;
import io.github.gms.functions.secret.dto.SecretDto;
import io.github.gms.functions.secret.dto.SecretListDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface SecretService
	extends AbstractCrudService<SaveSecretRequestDto, SaveEntityResponseDto, SecretDto, SecretListDto>, CountService, BatchDeletionService {
	
	String getSecretValue(Long id);
}
