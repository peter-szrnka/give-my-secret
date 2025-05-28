package io.github.gms.functions.setup;


import io.github.gms.common.abstraction.GmsController;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.dto.SimpleResponseDto;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.UserRole;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;
import io.github.gms.common.types.SkipSecurityTestCheck;
import io.github.gms.functions.user.SaveUserRequestDto;
import io.github.gms.functions.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/setup")
@SkipSecurityTestCheck
@AuditTarget(EventTarget.ADMIN_USER)
@Profile(CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO)
public class SetupController implements GmsController {

	private final SetupService setupService;

    @GetMapping(value = "/step_back", produces = "plain/text")
    @Audited(operation = EventOperation.SETUP)
    public String stepBack() {
        initMdc();
        return setupService.stepBack();
    }

    @GetMapping("/current_super_admin")
    @Audited(operation = EventOperation.SETUP)
    public UserDto getCurrentSuperAdmin() {
        initMdc();
        return setupService.getCurrentSuperAdmin();
    }
    @PostMapping("/initial")
    @Audited(operation = EventOperation.SETUP)
    public SimpleResponseDto saveInitialStep() {
        initMdc();
        return setupService.saveInitialStep();
    }

	@PostMapping("/user")
	@Audited(operation = EventOperation.SETUP)
	public SaveEntityResponseDto saveAdminUser(@RequestBody SaveUserRequestDto dto) {
		initMdc();

		if (dto.getRole() != null) {
			dto.setRole(UserRole.ROLE_ADMIN);
		}

		return setupService.saveAdminUser(dto);
	}

	@PostMapping("/properties")
	@Audited(operation = EventOperation.SETUP)
	public SimpleResponseDto saveSystemProperties(@RequestBody SetupSystemPropertiesDto dto) {
		initMdc();
		return setupService.saveSystemProperties(dto);
	}

    @PostMapping("/org_data")
    @Audited(operation = EventOperation.SETUP)
    public SimpleResponseDto saveOrganizationData(@RequestBody SetupSystemPropertiesDto dto) {
        initMdc();
        return setupService.saveOrganizationData(dto);
    }

    @PostMapping("/complete")
    @Audited(operation = EventOperation.SETUP)
    public SimpleResponseDto completeSetup() {
        initMdc();
        return setupService.completeSetup();
    }

	private static void initMdc() {
		MDC.put(MdcParameter.USER_NAME.getDisplayName(), "setup");
		MDC.put(MdcParameter.USER_ID.getDisplayName(), "0");
	}
}
