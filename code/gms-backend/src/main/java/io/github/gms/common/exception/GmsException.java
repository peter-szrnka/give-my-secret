package io.github.gms.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class GmsException extends RuntimeException {
	
	private static final long serialVersionUID = 1070662056072166798L;
	
	public GmsException(String message) {
		super(message);
	}

	public GmsException(Exception e) {
		super(e);
	}
}
