package io.github.gms.auth.model;

import io.github.gms.auth.types.AuthResponsePhase;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.common.types.Sensitive;
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
	@Sensitive
	private String token;
	@Sensitive
	private String refreshToken;
	@Builder.Default
	private AuthResponsePhase phase = AuthResponsePhase.FAILED;
}