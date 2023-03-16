package io.github.gms.common.controller;


import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Sets;

import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.UserRole;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.dto.SaveUserRequestDto;
import io.github.gms.secure.service.UserService;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/setup")
@AuditTarget(EventTarget.ADMIN_USER)
public class SetupController {
	
	@Autowired
	private UserService userService;

	@PostMapping("/user")
	@Audited(operation = EventOperation.SETUP)
	public @ResponseBody SaveEntityResponseDto saveAdminUser(@RequestBody SaveUserRequestDto dto) {
		MDC.put(MdcParameter.USER_NAME.getDisplayName(), "setup");
		MDC.put(MdcParameter.USER_ID.getDisplayName(), "0");

		if (dto.getRoles().isEmpty()) {
			dto.setRoles(Sets.newHashSet(UserRole.ROLE_ADMIN));
		}

		return userService.saveAdminUser(dto);
	}
}
