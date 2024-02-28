package io.github.gms.common.model;

import io.github.gms.functions.keystore.KeystoreEntity;
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
