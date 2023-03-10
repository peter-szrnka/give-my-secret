package io.github.gms.secure.dto;

import java.io.Serializable;
import java.util.List;

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
public class IdNamePairListDto implements Serializable {

	private static final long serialVersionUID = -4136554261517005342L;

	private List<IdNamePairDto> resultList;
}
