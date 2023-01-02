package io.github.gms.secure.service;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import io.github.gms.common.entity.SecretEntity;
import io.github.gms.common.model.KeystorePair;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface KeystoreDataService {

	KeystorePair getKeystoreData(SecretEntity secretEntity) throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException;
}
