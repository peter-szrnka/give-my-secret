package io.github.gms.common.model;

import java.security.KeyStore;

import io.github.gms.common.entity.KeystoreEntity;
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

	private KeystoreEntity entity;
	private KeyStore keystore;
}
