package io.github.gms.api.service;

import io.github.gms.secure.dto.GetSecretRequestDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface ApiService {

	String getSecret(GetSecretRequestDto dto);
}
