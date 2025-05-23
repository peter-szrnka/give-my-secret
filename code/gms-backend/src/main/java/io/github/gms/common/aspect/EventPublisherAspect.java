package io.github.gms.common.aspect;

import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;
import io.github.gms.common.model.UserEvent;
import io.github.gms.functions.event.EventService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Aspect
@Component
@RequiredArgsConstructor
public class EventPublisherAspect {

	private final EventService service;

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

	    AuditTarget source = method.getAnnotation(AuditTarget.class);
	    Audited auditSettings = method.getAnnotation(Audited.class);
	    
	    if (source == null) {
	    	source = joinPoint.getTarget().getClass().getAnnotation(AuditTarget.class);
	    }

	    Object result = joinPoint.proceed();
		
	    if (source != null && auditSettings != null) {
	    	service.saveUserEvent(new UserEvent(auditSettings.operation(), source.value()));
	    }
	    
	    return result;
	}
}
