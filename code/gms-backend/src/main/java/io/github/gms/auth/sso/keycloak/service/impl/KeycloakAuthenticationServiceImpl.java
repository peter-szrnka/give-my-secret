package io.github.gms.auth.sso.keycloak.service.impl;

import io.github.gms.auth.AuthenticationService;
import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.auth.sso.keycloak.converter.KeycloakConverter;
import io.github.gms.auth.sso.keycloak.model.IntrospectResponse;
import io.github.gms.auth.sso.keycloak.service.KeycloakIntrospectService;
import io.github.gms.auth.sso.keycloak.service.KeycloakLoginService;
import io.github.gms.auth.types.AuthResponsePhase;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.util.Constants;
import io.github.gms.functions.user.UserEntity;
import io.github.gms.functions.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_KEYCLOAK_SSO;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Profile(value = { CONFIG_AUTH_TYPE_KEYCLOAK_SSO })
public class KeycloakAuthenticationServiceImpl implements AuthenticationService {

    private final KeycloakLoginService keycloakLoginService;
    private final KeycloakIntrospectService keycloakIntrospectService;
    private final KeycloakConverter converter;
    @Autowired
    private UserRepository userRepository;

    @Override
    public AuthenticationResponse authenticate(String username, String credential) {
        // Login with Keycloak
        try {
            Map<String, String> response = keycloakLoginService.login(username, credential);

            // Get user data
            IntrospectResponse introspectResponse =
                    keycloakIntrospectService.getUserDetails(response.get(Constants.ACCESS_TOKEN), response.get(Constants.REFRESH_TOKEN));

            UserInfoDto userInfoDto = converter.toUserInfoDto(introspectResponse);
            addUserId(userInfoDto);

            return AuthenticationResponse.builder()
                    .currentUser(userInfoDto)
                    .phase(AuthResponsePhase.COMPLETED)
                    .token(response.get(Constants.ACCESS_TOKEN))
                    .refreshToken(response.get(Constants.REFRESH_TOKEN))
                    .build();
        } catch (Exception e) {
            return AuthenticationResponse.builder().build();
        }
    }

    private void addUserId(UserInfoDto userInfoDto) {
        UserEntity entity = userRepository.findByUsername(userInfoDto.getUsername())
                .orElse(new UserEntity());

        if (entity.getId() == null) {
            entity.setName(userInfoDto.getName());
            entity.setUsername(userInfoDto.getUsername());
            entity.setStatus(EntityStatus.ACTIVE);
            entity.setEmail(userInfoDto.getEmail());
            entity.setRoles(userInfoDto.getRoles().stream().map(Enum::name).collect(Collectors.joining(";")));
            entity = userRepository.save(entity);
        }

        userInfoDto.setId(entity.getId());
    }

    @Override
    public void logout() {
        keycloakLoginService.logout();
    }
}
