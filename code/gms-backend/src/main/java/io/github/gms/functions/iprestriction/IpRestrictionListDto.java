package io.github.gms.functions.iprestriction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IpRestrictionListDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 6273137971448067058L;
	@Builder.Default
	private List<IpRestrictionDto> resultList = new ArrayList<>();
	private long totalElements;
}
