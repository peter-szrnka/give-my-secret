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
public class IdNamePairDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 7563067646230827925L;

	private Long id;
	private String name;
}
