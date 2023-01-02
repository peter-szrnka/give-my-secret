package io.github.gms.secure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@AllArgsConstructor
public class GetSecretRequestDto {

	private String apiKey;
	private String secretId;
}
