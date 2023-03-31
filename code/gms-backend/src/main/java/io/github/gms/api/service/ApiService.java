package io.github.gms.api.service;

import io.github.gms.secure.dto.GetSecretRequestDto;

import java.util.Map;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface ApiService {

	Map<String, String> getSecret(GetSecretRequestDto dto);
}
