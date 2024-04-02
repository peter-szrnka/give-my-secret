package io.github.gms.auth.sso.keycloak.converter;

import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.auth.sso.keycloak.model.IntrospectResponse;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.UserRole;
import io.github.gms.functions.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_KEYCLOAK_SSO;

@Component
@RequiredArgsConstructor
@Profile(value = { CONFIG_AUTH_TYPE_KEYCLOAK_SSO })
public class KeycloakConverterImpl implements KeycloakConverter {

    private final Clock clock;

    @Override
    public GmsUserDetails toUserDetails(IntrospectResponse response) {
        return GmsUserDetails.builder()
                .username(response.getUsername())
                .email(response.getEmail())
                .name(response.getName())
                .status("true".equals(response.getActive()) ? EntityStatus.ACTIVE : EntityStatus.DISABLED)
                .authorities(Set.of(getRoles(response)))
                .build();
    }

    @Override
    public UserInfoDto toUserInfoDto(IntrospectResponse response) {
        return UserInfoDto.builder()
                .name(response.getName())
                .username(response.getUsername())
                .email(response.getEmail())
                .role(getRoles(response))
                .build();
    }

    public UserEntity toNewEntity(UserEntity entity, UserInfoDto dto) {
        if (entity.getId() == null) {
            entity.setName(dto.getName());
            entity.setUsername(dto.getUsername());
            entity.setStatus(EntityStatus.ACTIVE);
            entity.setEmail(dto.getEmail());
            entity.setRole(dto.getRole());
            entity.setCreationDate(ZonedDateTime.now(clock));
            SecretGenerator secretGenerator = new DefaultSecretGenerator();
            entity.setMfaSecret(secretGenerator.generate());
        }

        return entity;
    }

    private static UserRole getRoles(IntrospectResponse response) {
        return response.getRealmAccess().getRoles().stream().map(UserRole::getByName)
                .filter(Objects::nonNull)
                .toList().getFirst();
    }
}
