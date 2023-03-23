package io.github.gms.secure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiKeyListDto implements Serializable {

	private static final long serialVersionUID = -8839092933626161502L;
	private List<ApiKeyDto> resultList;
}
