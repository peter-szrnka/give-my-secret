package io.github.gms.common.controller;


import com.google.common.collect.Sets;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.UserRole;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.functions.user.SaveUserRequestDto;
import io.github.gms.functions.user.UserService;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/setup")
@AuditTarget(EventTarget.ADMIN_USER)
public class SetupController {

	private final UserService userService;

	public SetupController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/user")
	@Audited(operation = EventOperation.SETUP)
	public SaveEntityResponseDto saveAdminUser(@RequestBody SaveUserRequestDto dto) {
		MDC.put(MdcParameter.USER_NAME.getDisplayName(), "setup");
		MDC.put(MdcParameter.USER_ID.getDisplayName(), "0");

		if (dto.getRoles().isEmpty()) {
			dto.setRoles(Sets.newHashSet(UserRole.ROLE_ADMIN));
		}

		return userService.saveAdminUser(dto);
	}
}
