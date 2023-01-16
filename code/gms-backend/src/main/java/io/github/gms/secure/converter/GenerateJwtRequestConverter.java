package io.github.gms.secure.converter;

import java.util.Map;

import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.model.GenerateJwtRequest;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface GenerateJwtRequestConverter {

	GenerateJwtRequest toRequest(JwtConfigType jwtConfigType, String subject, Map<String, Object> claims);
}