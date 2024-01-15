package io.github.gms.secure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdNamePairListDto implements Serializable {

	@Serial
	private static final long serialVersionUID = -4136554261517005342L;

	private List<IdNamePairDto> resultList;
}
