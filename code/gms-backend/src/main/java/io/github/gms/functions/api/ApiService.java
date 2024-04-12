package io.github.gms.functions.api;

import io.github.gms.functions.secret.GetSecretRequestDto;
import io.github.gms.functions.secret.SecretEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiService {

	private final SecretPreparationService secretPreparationService;
	private final SecretValueProviderService secretValueProviderService;

	public Map<String, String> getSecret(GetSecretRequestDto dto) {
		log.info("Searching for secret={}", dto.getSecretId());

		// Validate API key, user, then get the secret
		SecretEntity secretEntity = secretPreparationService.getSecretEntity(dto);

		// Validate the keystore & retrieve the secret (cached)
		return secretValueProviderService.getSecretValue(secretEntity);
	}
}
