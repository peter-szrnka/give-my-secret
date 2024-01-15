package io.github.gms.secure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecretListDto implements Serializable {

	@Serial
	private static final long serialVersionUID = -232796913804612186L;

	private List<SecretDto> resultList;
	private long totalElements;
}
