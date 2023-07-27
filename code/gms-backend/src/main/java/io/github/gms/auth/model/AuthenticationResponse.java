package io.github.gms.auth.model;

import io.github.gms.auth.types.AuthResponsePhase;
import io.github.gms.secure.dto.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

	private UserInfoDto currentUser;
	private String token;
	private String refreshToken;
	@Builder.Default
	private AuthResponsePhase phase = AuthResponsePhase.FAILED;
}