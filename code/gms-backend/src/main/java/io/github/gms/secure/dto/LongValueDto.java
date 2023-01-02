package io.github.gms.secure.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
