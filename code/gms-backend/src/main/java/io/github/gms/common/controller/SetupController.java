package io.github.gms.common.controller;


import io.github.gms.common.abstraction.GmsController;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.UserRole;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;
import io.github.gms.common.types.SkipSecurityTestCheck;
import io.github.gms.functions.user.SaveUserRequestDto;
import io.github.gms.functions.user.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
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
@RequiredArgsConstructor
@RequestMapping("/setup")
@SkipSecurityTestCheck
@AuditTarget(EventTarget.ADMIN_USER)
@Profile(CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO)
public class SetupController implements GmsController {

	private final UserService userService;

	@PostMapping("/user")
	@Audited(operation = EventOperation.SETUP)
	public SaveEntityResponseDto saveAdminUser(@RequestBody SaveUserRequestDto dto) {
		MDC.put(MdcParameter.USER_NAME.getDisplayName(), "setup");
		MDC.put(MdcParameter.USER_ID.getDisplayName(), "0");

		if (dto.getRole() != null) {
			dto.setRole(UserRole.ROLE_ADMIN);
		}

		return userService.saveAdminUser(dto);
	}
}
