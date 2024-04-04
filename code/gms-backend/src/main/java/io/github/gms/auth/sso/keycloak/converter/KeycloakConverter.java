package io.github.gms.auth.sso.keycloak.converter;

import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.auth.sso.keycloak.model.IntrospectResponse;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.functions.user.UserEntity;

public interface KeycloakConverter {

    GmsUserDetails toUserDetails(IntrospectResponse response);

    UserInfoDto toUserInfoDto(IntrospectResponse response);

    UserEntity toEntity(UserEntity entity, UserInfoDto dto);
}
