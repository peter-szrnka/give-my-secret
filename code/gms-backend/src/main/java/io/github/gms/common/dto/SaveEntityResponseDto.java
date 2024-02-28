package io.github.gms.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class SaveEntityResponseDto extends SimpleResponseDto {

	@Serial
	private static final long serialVersionUID = 8978351781176023545L;

	private Long entityId;
}
