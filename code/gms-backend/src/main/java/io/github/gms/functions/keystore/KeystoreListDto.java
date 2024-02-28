package io.github.gms.functions.keystore;

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
public class KeystoreListDto implements Serializable {

	@Serial
	private static final long serialVersionUID = -7683988681730326414L;
	private List<KeystoreDto> resultList;
	private long totalElements;
}
