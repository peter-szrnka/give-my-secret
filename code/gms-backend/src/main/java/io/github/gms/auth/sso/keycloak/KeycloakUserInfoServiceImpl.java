package io.github.gms.auth.sso.keycloak;

import io.github.gms.auth.sso.OAuthService;
import io.github.gms.auth.sso.keycloak.config.KeycloakSettings;
import io.github.gms.auth.sso.keycloak.model.IntrospectResponse;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.common.enums.UserRole;
import io.github.gms.functions.user.UserInfoService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.WebUtils;

import java.util.Objects;
import java.util.stream.Collectors;

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

    private final OAuthService oAuthService;
    private final KeycloakSettings keycloakSettings;

    @Override
    public UserInfoDto getUserInfo(HttpServletRequest request) {
        Cookie accessJwtCookie = WebUtils.getCookie(request, ACCESS_JWT_TOKEN);
        Cookie refreshJwtCookie = WebUtils.getCookie(request, REFRESH_JWT_TOKEN);

        if (accessJwtCookie == null || refreshJwtCookie == null) {
            throw new AccessDeniedException("Invalid token!");
        }

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", keycloakSettings.getClientId());
        requestBody.add("client_secret", keycloakSettings.getClientSecret());
        requestBody.add("token", accessJwtCookie.getValue());
        requestBody.add("refresh_token", refreshJwtCookie.getValue());

        IntrospectResponse response = oAuthService.callEndpoint(keycloakSettings.getIntrospectUrl(), requestBody, IntrospectResponse.class);
        return UserInfoDto.builder()
                .email(response.getEmail())
                .name(response.getName())
                .username(response.getUsername())
                .roles(response.getRealmAccess().getRoles().stream().map(UserRole::getByName)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet()))
                .build();
    }
}
