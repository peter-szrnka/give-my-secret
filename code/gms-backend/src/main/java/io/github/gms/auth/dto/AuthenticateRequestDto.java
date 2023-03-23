package io.github.gms.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticateRequestDto implements Serializable {

	private static final long serialVersionUID = 3857918691961411838L;

	private String username;
	private String credential;
}
