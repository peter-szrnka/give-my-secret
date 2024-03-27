package io.github.gms.auth.sso.keycloak;

import io.github.gms.auth.UserAuthService;
import io.github.gms.auth.sso.OAuthService;
import io.github.gms.auth.sso.keycloak.config.KeycloakSettings;
import io.github.gms.auth.sso.keycloak.model.IntrospectResponse;
import io.github.gms.functions.user.UserConverter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.WebUtils;

import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_KEYCLOAK_SSO;
import static io.github.gms.common.util.Constants.REFRESH_JWT_TOKEN;

@Service
@RequiredArgsConstructor
@Profile(CONFIG_AUTH_TYPE_KEYCLOAK_SSO)
public class KeycloakUserAuthServiceImpl implements UserAuthService {

    private final OAuthService oAuthService;
    private final KeycloakSettings keycloakSettings;
    private final HttpServletRequest httpServletRequest;
    private final UserConverter userConverter;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Cookie accessJwtCookie = WebUtils.getCookie(httpServletRequest, ACCESS_JWT_TOKEN);
        Cookie refreshJwtCookie = WebUtils.getCookie(httpServletRequest, REFRESH_JWT_TOKEN);


        if (accessJwtCookie == null || refreshJwtCookie == null) {
            throw new UsernameNotFoundException("User not found!");
        }

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", keycloakSettings.getClientId());
        requestBody.add("client_secret", keycloakSettings.getClientSecret());
        requestBody.add("token", accessJwtCookie.getValue());
        requestBody.add("refresh_token", refreshJwtCookie.getValue());

        IntrospectResponse response = oAuthService.callEndpoint(keycloakSettings.getIntrospectUrl(), requestBody, IntrospectResponse.class);
        return userConverter.toUserDetails(response);
    }
}
