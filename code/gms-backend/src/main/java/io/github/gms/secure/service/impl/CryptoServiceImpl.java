package io.github.gms.secure.service.impl;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.gms.common.entity.SecretEntity;
import io.github.gms.common.exception.GmsException;
import io.github.gms.common.model.KeystorePair;
import io.github.gms.secure.dto.SaveKeystoreRequestDto;
import io.github.gms.secure.service.CryptoService;
import io.github.gms.secure.service.KeystoreDataService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
public class CryptoServiceImpl implements CryptoService {

	@Autowired
	private KeystoreDataService keystoreDataService;
	
	@Override
	public void validateKeyStoreFile(SaveKeystoreRequestDto dto, byte[] fileContent) {
		try {
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			keystore.load(new ByteArrayInputStream(fileContent), dto.getCredential().toCharArray());
			
			// Encrypt
			PublicKey publicKey = keystore.getCertificate(dto.getAlias()).getPublicKey();
			Cipher encrypt = Cipher.getInstance(publicKey.getAlgorithm());
			encrypt.init(Cipher.ENCRYPT_MODE, publicKey);
			
			byte[] encryptedMessage = encrypt.doFinal("test".getBytes(StandardCharsets.UTF_8));
			
			// Decrypt
			PrivateKey privateKey = (PrivateKey) keystore.getKey(dto.getAlias(), dto.getAliasCredential().toCharArray());
			Cipher decrypt = Cipher.getInstance(privateKey.getAlgorithm());
			decrypt.init(Cipher.DECRYPT_MODE, privateKey);
			decrypt.doFinal(encryptedMessage);
		} catch (Exception e) {
			log.error("Keystore cannot be loaded!", e);
			throw new GmsException(e);
		}
	}

	@Override
	public String decrypt(SecretEntity secretEntity) {
		try {
			KeystorePair keyPairData = keystoreDataService.getKeystoreData(secretEntity);
			PrivateKey privateKey = (PrivateKey) keyPairData.getKeystore().getKey(keyPairData.getEntity().getAlias(),
					keyPairData.getEntity().getAliasCredential().toCharArray());

			Cipher decrypt = Cipher.getInstance(privateKey.getAlgorithm());
			decrypt.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] decryptedMessage = decrypt.doFinal(Base64.getDecoder().decode(secretEntity.getValue().getBytes(StandardCharsets.UTF_8)));
			
			return new String(decryptedMessage, StandardCharsets.UTF_8);
		} catch (Exception e) {
			log.warn("Decrypt failed!", e);
			throw new GmsException(e);
		}
	}

	@Override
	public void encrypt(SecretEntity secretEntity) {
		try {
			KeystorePair keyPairData = keystoreDataService.getKeystoreData(secretEntity);
			PublicKey publicKey = keyPairData.getKeystore().getCertificate(keyPairData.getEntity().getAlias()).getPublicKey();

			Cipher encrypt = Cipher.getInstance(publicKey.getAlgorithm());
			encrypt.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] encryptedMessage = encrypt.doFinal(secretEntity.getValue().getBytes(StandardCharsets.UTF_8));

			secretEntity.setValue(Base64.getEncoder().withoutPadding().encodeToString(encryptedMessage));
		} catch (Exception e) {
			log.warn("Encrypt failed!", e);
			throw new GmsException(e);
		}
	}
}
