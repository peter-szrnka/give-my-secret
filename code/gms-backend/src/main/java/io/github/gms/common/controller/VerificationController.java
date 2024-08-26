package io.github.gms.common.controller;

import io.github.gms.auth.VerificationService;
import io.github.gms.auth.dto.AuthenticateResponseDto;
import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.auth.types.AuthResponsePhase;
import io.github.gms.common.abstraction.AbstractLoginController;
import io.github.gms.common.dto.LoginVerificationRequestDto;
import io.github.gms.common.types.SkipSecurityTestCheck;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/")
@SkipSecurityTestCheck
@Profile(CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO)
public class VerificationController extends AbstractLoginController {

    private final VerificationService service;

    public VerificationController(
            VerificationService verificationService,
            SystemPropertyService systemPropertyService,
            @Value("${config.cookie.secure}")  boolean secure) {
        super(systemPropertyService, secure);
        this.service = verificationService;
    }

    @PostMapping("verify")
    public ResponseEntity<AuthenticateResponseDto> verify(@RequestBody LoginVerificationRequestDto dto) {
        AuthenticationResponse authenticateResult = service.verify(dto);

        if (AuthResponsePhase.FAILED == authenticateResult.getPhase()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        return ResponseEntity.ok().headers(addHeaders(authenticateResult)).body(AuthenticateResponseDto.builder()
                .currentUser(authenticateResult.getCurrentUser())
                .phase(authenticateResult.getPhase())
                .build());
    }
}
