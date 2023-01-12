package io.github.gms.common.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateJwtRequest {
	private String subject;
	private String algorithm;
	private Long expirationDateInSeconds;
	private Map<String, Object> claims;
}