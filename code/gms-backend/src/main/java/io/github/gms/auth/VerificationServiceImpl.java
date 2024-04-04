package io.github.gms.auth;

import dev.samstevens.totp.code.CodeVerifier;
import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.auth.service.TokenGeneratorService;
import io.github.gms.auth.types.AuthResponsePhase;
import io.github.gms.common.dto.LoginVerificationRequestDto;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.functions.user.UserConverter;
import io.github.gms.functions.user.UserLoginAttemptManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Profile(CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO)
public class VerificationServiceImpl implements VerificationService {

    private final UserAuthService userAuthService;
    private final UserConverter converter;
    private final CodeVerifier verifier;
    private final UserLoginAttemptManagerService userLoginAttemptManagerService;
    private final TokenGeneratorService tokenGeneratorService;

    @Override
    public AuthenticationResponse verify(LoginVerificationRequestDto dto) {
        try {
            if (userLoginAttemptManagerService.isBlocked(dto.getUsername())) {
                return AuthenticationResponse.builder()
                        .phase(AuthResponsePhase.BLOCKED)
                        .build();
            }

            GmsUserDetails userDetails = (GmsUserDetails) userAuthService.loadUserByUsername(dto.getUsername());

            if (Boolean.FALSE.equals(userDetails.getAccountNonLocked())) { // User locked in LDAP
                return AuthenticationResponse.builder()
                        .phase(AuthResponsePhase.BLOCKED)
                        .build();
            }

            if (!verifier.isValidCode(userDetails.getMfaSecret(), dto.getVerificationCode())) {
                userLoginAttemptManagerService.updateLoginAttempt(dto.getUsername());
                return AuthenticationResponse.builder().phase(AuthResponsePhase.FAILED).build();
            }

            Map<JwtConfigType, String> authenticationDetails = tokenGeneratorService.getAuthenticationDetails(userDetails);

            // Verify codes
            return AuthenticationResponse.builder()
                    .currentUser(converter.toUserInfoDto(userDetails, false))
                    .phase(AuthResponsePhase.COMPLETED)
                    .token(authenticationDetails.get(JwtConfigType.ACCESS_JWT))
                    .refreshToken(authenticationDetails.get(JwtConfigType.REFRESH_JWT))
                    .build();
        } catch (Exception e) {
            return AuthenticationResponse.builder()
                    .phase(AuthResponsePhase.FAILED)
                    .build();
        }
    }
}
