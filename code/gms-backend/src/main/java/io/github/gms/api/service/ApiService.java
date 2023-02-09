package io.github.gms.api.service;

import io.github.gms.secure.dto.ApiResponseDto;
import io.github.gms.secure.dto.GetSecretRequestDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface ApiService {

	ApiResponseDto getSecret(GetSecretRequestDto dto);
}
