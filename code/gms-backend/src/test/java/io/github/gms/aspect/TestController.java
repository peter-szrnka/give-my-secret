package io.github.gms.aspect;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@AuditTarget(EventTarget.ADMIN_USER)
public class TestController {

	@ResponseBody
	@GetMapping("/test")
	@Audited(operation = EventOperation.GET_BY_ID)
	public String test() {
		return "OK";
	}
	
	@ResponseBody
	@GetMapping("/test2")
	@AuditTarget(EventTarget.ADMIN_USER)
	@Audited(operation = EventOperation.GET_BY_ID)
	public String test2() {
		return "OK";
	}
}
