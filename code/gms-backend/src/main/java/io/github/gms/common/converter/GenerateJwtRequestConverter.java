package io.github.gms.common.converter;

import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.model.GenerateJwtRequest;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class GenerateJwtRequestConverter {

	private final SystemPropertyService systemPropertyService;

	public GenerateJwtRequest toRequest(JwtConfigType jwtConfigType, String subject, Map<String, Object> claims) {
		String algorithm = systemPropertyService.get(jwtConfigType.getAlgorithmProperty());
		Long expirationDateInSeconds = systemPropertyService.getLong(jwtConfigType.getExpirationSecondsProperty());

		return GenerateJwtRequest.builder().subject(subject).algorithm(algorithm)
				.expirationDateInSeconds(expirationDateInSeconds)
				.claims(claims)
				.build();
	}
}