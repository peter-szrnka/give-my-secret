package io.github.gms.aspect;

import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
public class Test2Controller {
	
	@ResponseBody
	@GetMapping("/test3")
	@AuditTarget(EventTarget.ADMIN_USER)
	public String test() {
		return "OK";
	}

	@ResponseBody
	@GetMapping("/test4")
	@Audited(operation = EventOperation.GET_BY_ID)
	public String test2() {
		return "OK";
	}
}
