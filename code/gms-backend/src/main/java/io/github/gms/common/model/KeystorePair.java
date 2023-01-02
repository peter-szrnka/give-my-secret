package io.github.gms.common.model;

import java.io.Serializable;
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
public class KeystorePair implements Serializable {

	private static final long serialVersionUID = -3509296503598020496L;

	private KeystoreEntity entity;
	private KeyStore keystore;
}
