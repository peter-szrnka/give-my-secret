package io.github.gms.auth.sso.keycloak.converter;

import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.auth.sso.keycloak.model.IntrospectResponse;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.UserRole;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_KEYCLOAK_SSO;

@Component
@Profile(value = { CONFIG_AUTH_TYPE_KEYCLOAK_SSO })
public class KeycloakConverterImpl implements KeycloakConverter {
    @Override
    public GmsUserDetails toUserDetails(IntrospectResponse response) {
        return GmsUserDetails.builder()
                .username(response.getUsername())
                .email(response.getEmail())
                .name(response.getName())
                .status("true".equals(response.getActive()) ? EntityStatus.ACTIVE : EntityStatus.DISABLED)
                .authorities(getRoles(response))
                .build();
    }

    @Override
    public UserInfoDto toUserInfoDto(IntrospectResponse response) {
        return UserInfoDto.builder()
                .name(response.getName())
                .username(response.getUsername())
                .email(response.getEmail())
                .roles(getRoles(response))
                .build();
    }

    private Set<UserRole> getRoles(IntrospectResponse response) {
        return response.getRealmAccess().getRoles().stream().map(UserRole::getByName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
