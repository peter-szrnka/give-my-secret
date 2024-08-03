package io.github.gms;

import com.google.common.base.Throwables;
import io.github.gms.common.dto.ErrorResponseDto;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.types.ErrorCode;
import io.github.gms.common.types.GmsException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Clock;
import java.time.ZonedDateTime;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@ControllerAdvice
public class GmsExceptionHandler {

	private final Clock clock;

	public GmsExceptionHandler(Clock clock) {
		this.clock = clock;
	}

	@ExceptionHandler(GmsException.class)
	public ResponseEntity<ErrorResponseDto> handleGmsException(GmsException ex) {
		log.error("GmsException handled", ex);
		return new ResponseEntity<>(new ErrorResponseDto(
				Throwables.getRootCause(ex).getMessage(),
				MDC.get(MdcParameter.CORRELATION_ID.getDisplayName()),
				ZonedDateTime.now(clock),
				ex.getErrorCode().getCode()
		), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(AccessDeniedException ex) {
		return getResponse(ex, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(MissingRequestHeaderException.class)
	public ResponseEntity<ErrorResponseDto> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
		return getResponse(ex, HttpStatus.BAD_REQUEST);
	}

	private ResponseEntity<ErrorResponseDto> getResponse(Exception ex, HttpStatus httpStatus) {
		return new ResponseEntity<>(new ErrorResponseDto(
				Throwables.getRootCause(ex).getMessage(),
				MDC.get(MdcParameter.CORRELATION_ID.getDisplayName()),
				ZonedDateTime.now(clock),
				ErrorCode.GMS_000.getCode()
		), httpStatus);
	}
}