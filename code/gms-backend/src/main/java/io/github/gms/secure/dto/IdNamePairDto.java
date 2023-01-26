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
public class IdNamePairDto implements Serializable {

	private static final long serialVersionUID = 7563067646230827925L;

	private Long id;
	private String name;
}
