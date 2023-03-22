package io.github.gms.secure.converter.impl;

import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.model.GenerateJwtRequest;
import io.github.gms.secure.converter.GenerateJwtRequestConverter;
import io.github.gms.secure.service.SystemPropertyService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
public class GenerateJwtRequestConverterImpl implements GenerateJwtRequestConverter {

	private final SystemPropertyService systemPropertyService;

	public GenerateJwtRequestConverterImpl(SystemPropertyService systemPropertyService) {
		this.systemPropertyService = systemPropertyService;
	}

	@Override
	public GenerateJwtRequest toRequest(JwtConfigType jwtConfigType, String subject, Map<String, Object> claims) {
		String algorithm = systemPropertyService.get(jwtConfigType.getAlgorithmProperty());
		Long expirationDateInSeconds = systemPropertyService.getLong(jwtConfigType.getExpirationSecondsProperty());

		return GenerateJwtRequest.builder().subject(subject).algorithm(algorithm)
				.expirationDateInSeconds(expirationDateInSeconds)
				.claims(claims)
				.build();
	}
}