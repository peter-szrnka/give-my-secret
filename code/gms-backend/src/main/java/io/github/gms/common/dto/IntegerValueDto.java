package io.github.gms.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntegerValueDto implements Serializable {

	@Serial
	private static final long serialVersionUID = -3468984767469124094L;

	private Integer value;
}
