package io.github.gms.auth.sso.keycloak.service.impl;

import io.github.gms.auth.AuthenticationService;
import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.auth.sso.keycloak.converter.KeycloakConverter;
import io.github.gms.auth.sso.keycloak.model.IntrospectResponse;
import io.github.gms.auth.sso.keycloak.model.LoginResponse;
import io.github.gms.auth.sso.keycloak.service.KeycloakIntrospectService;
import io.github.gms.auth.sso.keycloak.service.KeycloakLoginService;
import io.github.gms.auth.types.AuthResponsePhase;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.functions.user.UserEntity;
import io.github.gms.functions.user.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_KEYCLOAK_SSO;
import static io.github.gms.common.util.Constants.REFRESH_JWT_TOKEN;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Profile(value = { CONFIG_AUTH_TYPE_KEYCLOAK_SSO })
public class KeycloakAuthenticationServiceImpl implements AuthenticationService {

    private final KeycloakLoginService keycloakLoginService;
    private final KeycloakIntrospectService keycloakIntrospectService;
    private final KeycloakConverter converter;
    private final UserRepository userRepository;
    private final HttpServletRequest httpServletRequest;

    @Override
    public AuthenticationResponse authenticate(String username, String credential) {
        Cookie accessJwtCookie = WebUtils.getCookie(httpServletRequest, ACCESS_JWT_TOKEN);
        Cookie refreshJwtCookie = WebUtils.getCookie(httpServletRequest, REFRESH_JWT_TOKEN);

        if (accessJwtCookie != null && refreshJwtCookie != null) {
            UserInfoDto userInfoDto = getUserDetails(accessJwtCookie.getValue(), refreshJwtCookie.getValue());
            return AuthenticationResponse.builder()
                    .currentUser(userInfoDto)
                    .phase(AuthResponsePhase.ALREADY_LOGGED_IN)
                    .token(accessJwtCookie.getValue())
                    .refreshToken(refreshJwtCookie.getValue())
                    .build();
        }

        // Login with Keycloak
        try {
            LoginResponse response = keycloakLoginService.login(username, credential);

            // Get user data
            UserInfoDto userInfoDto = getUserDetails(response.getAccessToken(), response.getRefreshToken());

            if (userInfoDto == null) {
                return AuthenticationResponse.builder().build();
            }

            addUserId(userInfoDto);

            return AuthenticationResponse.builder()
                    .currentUser(userInfoDto)
                    .phase(AuthResponsePhase.COMPLETED)
                    .token(response.getAccessToken())
                    .refreshToken(response.getRefreshToken())
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error occurred during authentication!", e);
            return AuthenticationResponse.builder().build();
        }
    }

    private void addUserId(UserInfoDto userInfoDto) {
        UserEntity entity = userRepository.findByUsername(userInfoDto.getUsername())
                .orElse(new UserEntity());

        if (entity.getId() == null) {
            entity = userRepository.save(converter.toNewEntity(entity, userInfoDto));
        }
        // TODO Handle existing entities

        userInfoDto.setId(entity.getId());
    }

    private UserInfoDto getUserDetails(String accessToken, String refreshToken) {
        IntrospectResponse introspectResponse = keycloakIntrospectService.getUserDetails(accessToken, refreshToken);

        if (!"true".equals(introspectResponse.getActive())) {
            return null;
        }

        return converter.toUserInfoDto(introspectResponse);
    }

    @Override
    public void logout() {
        keycloakLoginService.logout();
    }
}
