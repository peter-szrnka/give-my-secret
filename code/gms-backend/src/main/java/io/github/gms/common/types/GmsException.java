package io.github.gms.common.types;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class GmsException extends RuntimeException {
	
	@Serial
	private static final long serialVersionUID = 1070662056072166798L;
	private ErrorCode errorCode;

	public GmsException(String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public GmsException(String message, Exception e, ErrorCode errorCode) {
		super(message, e);
		this.errorCode = errorCode;
	}

	public GmsException(Exception e, ErrorCode errorCode) {
		super(e);
		this.errorCode = errorCode;
	}
}
