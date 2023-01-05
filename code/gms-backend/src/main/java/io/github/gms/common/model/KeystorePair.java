package io.github.gms.common.model;

import java.security.KeyStore;

import io.github.gms.secure.entity.KeystoreAliasEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
