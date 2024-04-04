package io.github.gms.functions.user;

import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.service.JwtClaimService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Profile(CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO)
public class UserInfoServiceImpl implements UserInfoService {

	private final UserRepository repository;
	private final JwtClaimService jwtClaimService;

	@Override
	public UserInfoDto getUserInfo(HttpServletRequest request) {
		Cookie jwtTokenCookie = WebUtils.getCookie(request, ACCESS_JWT_TOKEN);

		if (jwtTokenCookie == null) {
			// We should not return an error, just simply return nothing
			return null;
		}

		Claims claims = jwtClaimService.getClaims(jwtTokenCookie.getValue());
		UserEntity entity = validateAndReturnUser(claims.get(MdcParameter.USER_ID.getDisplayName(), Long.class));

		if (entity == null) {
			return null;
		}

		return UserInfoDto.builder()
			.id(entity.getId())
			.name(entity.getName())
			.username(entity.getUsername())
			.role(entity.getRole())
			.email(entity.getEmail())
			.build();
	}

	private UserEntity validateAndReturnUser(Long userId) {
		return repository.findById(userId).orElse(null);
	}
}
