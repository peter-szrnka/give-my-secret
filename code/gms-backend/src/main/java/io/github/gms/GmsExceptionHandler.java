package io.github.gms;

import java.time.Clock;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.google.common.base.Throwables;

import io.github.gms.common.dto.ErrorResponseDto;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.exception.GmsException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@ControllerAdvice
public class GmsExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	private Clock clock;

	@ExceptionHandler(GmsException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ErrorResponseDto handleOtherException(HttpServletRequest request, HandlerMethod handlerMethod, GmsException ex) {
		log.error("GmsException handled", ex);
		return new ErrorResponseDto(Throwables.getRootCause(ex).getMessage(), MDC.get(MdcParameter.CORRELATION_ID.getDisplayName()), LocalDateTime.now(clock));
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Access forbidden")
	public @ResponseBody ErrorResponseDto handleOtherException(HttpServletRequest request, HandlerMethod handlerMethod, AccessDeniedException ex) {
		return new ErrorResponseDto(Throwables.getRootCause(ex).getMessage(), MDC.get(MdcParameter.CORRELATION_ID.getDisplayName()), LocalDateTime.now(clock));
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ErrorResponseDto handleOtherException(HttpServletRequest request, HandlerMethod handlerMethod, Exception ex) {
		log.error("Exception handled", ex);
		return new ErrorResponseDto(Throwables.getRootCause(ex).getMessage(), MDC.get(MdcParameter.CORRELATION_ID.getDisplayName()), LocalDateTime.now(clock));
	}
}