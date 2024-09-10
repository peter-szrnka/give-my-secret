package io.github.gms.auth.dto;

import io.github.gms.common.types.Sensitive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticateRequestDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 3857918691961411838L;

	@Sensitive
	private String username;
	@Sensitive
	private String credential;
}
