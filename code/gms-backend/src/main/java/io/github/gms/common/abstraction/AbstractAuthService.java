package io.github.gms.common.abstraction;

import io.github.gms.auth.UserAuthService;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.converter.GenerateJwtRequestConverter;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.UserRole;
import io.github.gms.common.model.GenerateJwtRequest;
import io.github.gms.common.service.JwtService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RequiredArgsConstructor
public abstract class AbstractAuthService {

	protected final JwtService jwtService;
	protected final SystemPropertyService systemPropertyService;
	protected final GenerateJwtRequestConverter generateJwtRequestConverter;
	protected final UserAuthService userAuthService;
	
	protected Map<JwtConfigType, String> getAuthenticationDetails(GmsUserDetails user) {
		Map<JwtConfigType, GenerateJwtRequest> input = Map.of(
				JwtConfigType.ACCESS_JWT, buildAccessJwtRequest(user.getUserId(), user.getUsername(), 
						user.getAuthorities().stream().map(authority -> UserRole.getByName(authority.getAuthority())).collect(Collectors.toSet())),
				JwtConfigType.REFRESH_JWT, buildRefreshTokenRequest(user.getUsername())
		);

		return jwtService.generateJwts(input);
	}
	
	private GenerateJwtRequest buildRefreshTokenRequest(String userName) {
		Map<String, Object> claims = Map.of(
				MdcParameter.USER_NAME.getDisplayName(), userName
		);
		return generateJwtRequestConverter.toRequest(JwtConfigType.REFRESH_JWT, userName, claims);
	}

	private GenerateJwtRequest buildAccessJwtRequest(Long userId, String userName, Set<UserRole> roles) {
		Map<String, Object> claims = Map.of(
				MdcParameter.USER_ID.getDisplayName(), userId,
				MdcParameter.USER_NAME.getDisplayName(), userName,
				"roles", roles
		);

		return generateJwtRequestConverter.toRequest(JwtConfigType.ACCESS_JWT, userName, claims);
	}
}
