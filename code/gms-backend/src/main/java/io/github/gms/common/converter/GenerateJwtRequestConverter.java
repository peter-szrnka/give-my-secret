package io.github.gms.common.converter;

import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.model.GenerateJwtRequest;

import java.util.Map;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface GenerateJwtRequestConverter {

	GenerateJwtRequest toRequest(JwtConfigType jwtConfigType, String subject, Map<String, Object> claims);
}