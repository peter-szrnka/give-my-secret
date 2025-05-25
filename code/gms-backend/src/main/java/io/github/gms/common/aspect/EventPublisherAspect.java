package io.github.gms.common.aspect;

import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.model.UserEvent;
import io.github.gms.common.service.GmsThreadLocalValues;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;
import io.github.gms.functions.event.EventSource;
import io.github.gms.functions.event.EventService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Clock;
import java.time.ZonedDateTime;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Aspect
@Component
@RequiredArgsConstructor
public class EventPublisherAspect {

	private final EventService service;
	private final Clock clock;

	@Pointcut("execution(* *.*(..))")
	public void allMethod() {
		// only a pointcut registration
	}
	
	@Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
	public void restController() {
		// only a pointcut registration
	}

	@Pointcut("@annotation(io.github.gms.common.types.Audited)")
	public void audited() {
		// only a pointcut registration
	}

	@Around("(audited() || restController()) && allMethod()")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
	    Method method = signature.getMethod();

	    AuditTarget auditTarget = method.getAnnotation(AuditTarget.class);
	    Audited auditSettings = method.getAnnotation(Audited.class);
	    
	    if (auditTarget == null) {
	    	auditTarget = joinPoint.getTarget().getClass().getAnnotation(AuditTarget.class);
	    }

		GmsThreadLocalValues.setEventSource(EventSource.UI);
		setCurrentUser();

	    Object result = joinPoint.proceed();
		
	    if (auditTarget != null && auditSettings != null && auditSettings.operation().isBasicAuditRequired()) {
			saveUserEvent(joinPoint, auditTarget.value(), auditSettings.operation());
	    }

		GmsThreadLocalValues.removeEventSource();
		GmsThreadLocalValues.removeUserId();
	    
	    return result;
	}

	private void setCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null) {
			return;
		}

		if (auth.getPrincipal() instanceof GmsUserDetails userDetails) {
            GmsThreadLocalValues.setUserId(userDetails.getUserId());
		}
	}

	private void saveUserEvent(ProceedingJoinPoint joinPoint, EventTarget auditTarget, EventOperation eventOperation) {
		Long entityId = eventOperation == EventOperation.GET_BY_ID ? (Long) joinPoint.getArgs()[0] : null;
		service.saveUserEvent(UserEvent.builder()
				.entityId(entityId)
				.userId(GmsThreadLocalValues.getUserId())
				.operation(eventOperation)
				.eventSource(EventSource.UI)
				.target(auditTarget)
				.eventDate(ZonedDateTime.now(clock))
				.build());
	}
}
