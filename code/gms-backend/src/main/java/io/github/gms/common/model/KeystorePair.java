package io.github.gms.common.model;

import io.github.gms.functions.keystore.KeystoreAliasEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.KeyStore;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeystorePair {

	private KeystoreAliasEntity entity;
	private KeyStore keystore;
}
