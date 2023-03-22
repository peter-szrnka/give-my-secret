package io.github.gms.secure.service.impl;

import com.google.common.io.Files;
import io.github.gms.common.exception.GmsException;
import io.github.gms.common.model.KeystorePair;
import io.github.gms.secure.entity.KeystoreAliasEntity;
import io.github.gms.secure.entity.KeystoreEntity;
import io.github.gms.secure.entity.SecretEntity;
import io.github.gms.secure.model.GetKeystore;
import io.github.gms.secure.repository.KeystoreAliasRepository;
import io.github.gms.secure.repository.KeystoreRepository;
import io.github.gms.secure.service.KeystoreDataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
public class KeystoreDataServiceImpl implements KeystoreDataService {

	private static final String SLASH = "/";

	private final KeystoreRepository keystoreRepository;
	private final KeystoreAliasRepository keystoreAliasRepository;
	private final String keystorePath;

	public KeystoreDataServiceImpl(KeystoreRepository keystoreRepository,
								   KeystoreAliasRepository keystoreAliasRepository,
								   @Value("${config.location.keystore.path}") String keystorePath) {
		this.keystoreRepository = keystoreRepository;
		this.keystoreAliasRepository = keystoreAliasRepository;
		this.keystorePath = keystorePath;
	}

	@Override
	public KeystorePair getKeystoreData(SecretEntity secretEntity)
			throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
		KeystoreAliasEntity keystoreAliasEntity = keystoreAliasRepository.findById(secretEntity.getKeystoreAliasId())
				.orElseThrow(() -> new GmsException("Invalid keystore alias!"));

		KeystoreEntity keystoreEntity = getKeystoreEntity(keystoreAliasEntity.getKeystoreId());
		KeyStore keystore = getKeyStore(GetKeystore.builder().keystoreEntity(keystoreEntity)
				.keystorePath(keystorePath + keystoreEntity.getUserId() + SLASH + keystoreEntity.getFileName())
				.build());

		return new KeystorePair(keystoreAliasEntity, keystore);
	}

	private KeyStore getKeyStore(GetKeystore request)
			throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException {
		KeystoreEntity keystoreEntity = request.getKeystoreEntity();

		File keystoreFile = new File(request.getKeystorePath());
		KeyStore keystore = KeyStore.getInstance(keystoreEntity.getType().name());
		keystore.load(new ByteArrayInputStream(Files.toByteArray(keystoreFile)),
				keystoreEntity.getCredential().toCharArray());

		return keystore;
	}

	private KeystoreEntity getKeystoreEntity(Long keystoreId) {
		return keystoreRepository.findById(keystoreId).orElseThrow(() -> new GmsException("Keystore entity not found!"));
	}
}
