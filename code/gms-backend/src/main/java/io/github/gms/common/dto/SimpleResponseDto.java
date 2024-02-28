package io.github.gms.common.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
public class SimpleResponseDto implements Serializable {
	@Serial
	private static final long serialVersionUID = -5324564162143551148L;

	private boolean success = true;
}
