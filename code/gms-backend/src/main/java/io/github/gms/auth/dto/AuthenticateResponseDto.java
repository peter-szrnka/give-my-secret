package io.github.gms.auth.dto;

import java.io.Serializable;

import io.github.gms.secure.dto.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticateResponseDto implements Serializable {

	private static final long serialVersionUID = 5395019181044919899L;

	private UserInfoDto currentUser;
	private String token;
	private String refreshToken;
}
