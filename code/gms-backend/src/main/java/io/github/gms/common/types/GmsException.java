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
	
	public GmsException(String message) {
		super(message);
	}

	public GmsException(String message, Exception e) {
		super(message, e);
	}

	public GmsException(Exception e) {
		super(e);
	}
}
