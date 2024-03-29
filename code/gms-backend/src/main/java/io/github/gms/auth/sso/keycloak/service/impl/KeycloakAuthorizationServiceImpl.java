package io.github.gms.auth.sso.keycloak.service.impl;

import io.github.gms.auth.AuthorizationService;
import io.github.gms.auth.model.AuthorizationResponse;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.auth.sso.keycloak.converter.KeycloakConverter;
import io.github.gms.auth.sso.keycloak.model.IntrospectResponse;
import io.github.gms.auth.sso.keycloak.service.KeycloakIntrospectService;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.functions.user.UserEntity;
import io.github.gms.functions.user.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.util.Map;
import java.util.Optional;

import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_KEYCLOAK_SSO;
import static io.github.gms.common.util.Constants.REFRESH_JWT_TOKEN;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Profile(value = { CONFIG_AUTH_TYPE_KEYCLOAK_SSO })
public class KeycloakAuthorizationServiceImpl implements AuthorizationService {

    private final KeycloakConverter converter;
    private final KeycloakIntrospectService keycloakIntrospectService;
    @Autowired
    private UserRepository userRepository;

    @Override
    public AuthorizationResponse authorize(HttpServletRequest request) {
        Cookie accessJwtCookie = WebUtils.getCookie(request, ACCESS_JWT_TOKEN);
        Cookie refreshJwtCookie = WebUtils.getCookie(request, REFRESH_JWT_TOKEN);

        if (accessJwtCookie == null || refreshJwtCookie == null) {
            return AuthorizationResponse.builder().responseStatus(HttpStatus.FORBIDDEN).errorMessage("Access denied!").build();
        }

        IntrospectResponse response = keycloakIntrospectService.getUserDetails(accessJwtCookie.getValue(), refreshJwtCookie.getValue());
        GmsUserDetails userDetails = converter.toUserDetails(response);

        Optional<UserEntity> userResult = userRepository.findByUsername(userDetails.getUsername());

        if (userResult.isEmpty()) {
            return AuthorizationResponse.builder().responseStatus(HttpStatus.FORBIDDEN).errorMessage("Access denied!").build();
        }

        userDetails.setUserId(userResult.get().getId());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        return AuthorizationResponse.builder()
                .authentication(authentication)
                .jwtPair(Map.of(
                        JwtConfigType.ACCESS_JWT, accessJwtCookie.getValue(),
                        JwtConfigType.REFRESH_JWT, refreshJwtCookie.getValue()
                ))
                .build();
    }
}
