package io.github.gms.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleResponseDto implements Serializable {
	@Serial
	private static final long serialVersionUID = -5324564162143551148L;

	@Builder.Default
	private boolean success = true;
}
