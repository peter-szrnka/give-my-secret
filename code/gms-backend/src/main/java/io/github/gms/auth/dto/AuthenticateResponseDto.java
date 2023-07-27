package io.github.gms.auth.dto;

import io.github.gms.auth.types.AuthResponsePhase;
import io.github.gms.secure.dto.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticateResponseDto implements Serializable {

	private static final long serialVersionUID = 5395019181044919899L;

	private UserInfoDto currentUser;
	@Builder.Default
	private AuthResponsePhase phase = AuthResponsePhase.FAILED;
}
