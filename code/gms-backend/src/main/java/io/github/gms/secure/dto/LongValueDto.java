package io.github.gms.secure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LongValueDto implements Serializable {

	private static final long serialVersionUID = 521651101912746662L;

	private Long value;
}
