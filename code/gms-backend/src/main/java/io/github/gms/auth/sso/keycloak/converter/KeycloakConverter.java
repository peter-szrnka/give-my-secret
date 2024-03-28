package io.github.gms.auth.sso.keycloak.converter;

import io.github.gms.auth.sso.keycloak.model.IntrospectResponse;
import io.github.gms.common.dto.UserInfoDto;
import org.springframework.security.core.userdetails.UserDetails;

public interface KeycloakConverter {

    UserDetails toUserDetails(IntrospectResponse response);

    UserInfoDto toUserInfoDto(IntrospectResponse introspectResponse);
}
