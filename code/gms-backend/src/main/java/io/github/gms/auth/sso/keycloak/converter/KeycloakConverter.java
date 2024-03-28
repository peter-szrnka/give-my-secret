package io.github.gms.auth.sso.keycloak.converter;

import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.auth.sso.keycloak.model.IntrospectResponse;
import io.github.gms.common.dto.UserInfoDto;

public interface KeycloakConverter {

    GmsUserDetails toUserDetails(IntrospectResponse response);

    UserInfoDto toUserInfoDto(IntrospectResponse response);
}
