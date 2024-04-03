package io.github.gms.auth.sso.keycloak.service.impl;

import io.github.gms.auth.sso.keycloak.model.IntrospectResponse;
import io.github.gms.auth.sso.keycloak.service.KeycloakIntrospectService;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.common.enums.UserRole;
import io.github.gms.functions.user.UserInfoService;
import io.github.gms.functions.user.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.util.Objects;

import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_KEYCLOAK_SSO;
import static io.github.gms.common.util.Constants.REFRESH_JWT_TOKEN;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Profile(CONFIG_AUTH_TYPE_KEYCLOAK_SSO)
public class KeycloakUserInfoServiceImpl implements UserInfoService {

    private final KeycloakIntrospectService keycloakIntrospectService;
    private final UserRepository userRepository;

    @Override
    public UserInfoDto getUserInfo(HttpServletRequest request) {
        Cookie accessJwtCookie = WebUtils.getCookie(request, ACCESS_JWT_TOKEN);
        Cookie refreshJwtCookie = WebUtils.getCookie(request, REFRESH_JWT_TOKEN);

        if (accessJwtCookie == null || refreshJwtCookie == null) {
            return UserInfoDto.builder().build();
        }

        ResponseEntity<IntrospectResponse> response = keycloakIntrospectService.getUserDetails(accessJwtCookie.getValue(), refreshJwtCookie.getValue());
        IntrospectResponse payload = response.getBody();
        if (!response.getStatusCode().is2xxSuccessful() || payload == null) {
            return null;
        }

        Long userId = userRepository.getIdByUsername(payload.getUsername()).orElse(-1L);

        if (userId.equals(-1L)) {
            return null;
        }

        return UserInfoDto.builder()
                .id(userId)
                .email(payload.getEmail())
                .name(payload.getName())
                .username(payload.getUsername())
                .role(payload.getRealmAccess().getRoles().stream().map(UserRole::getByName)
                        .filter(Objects::nonNull)
                        .toList().getFirst())
                .build();
    }
}
