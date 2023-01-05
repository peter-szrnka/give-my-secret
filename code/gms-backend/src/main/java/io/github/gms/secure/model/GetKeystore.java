package io.github.gms.secure.model;

import io.github.gms.secure.entity.KeystoreEntity;
import lombok.Builder;
import lombok.Data;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Builder
public class GetKeystore {

	private KeystoreEntity keystoreEntity;
	private String keystorePath;
}
