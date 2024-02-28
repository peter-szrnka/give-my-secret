package io.github.gms.functions.api;

import io.github.gms.functions.secret.GetSecretRequestDto;

import java.util.Map;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface ApiService {

	Map<String, String> getSecret(GetSecretRequestDto dto);
}
