package io.github.gms.common.service;

import io.github.gms.common.model.KeystorePair;
import io.github.gms.common.types.GmsException;
import io.github.gms.functions.keystore.KeystoreAliasDto;
import io.github.gms.functions.keystore.KeystoreDataService;
import io.github.gms.functions.keystore.SaveKeystoreRequestDto;
import io.github.gms.functions.secret.SecretEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.util.Base64;

import static io.github.gms.common.types.ErrorCode.GMS_001;
import static io.github.gms.common.types.ErrorCode.GMS_006;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoService {

	private final KeystoreDataService keystoreDataService;

	public void validateKeyStoreFile(SaveKeystoreRequestDto dto, byte[] fileContent) {
		try {
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			keystore.load(new ByteArrayInputStream(fileContent), dto.getCredential().toCharArray());

			for (KeystoreAliasDto aliasDto : dto.getAliases()) {
				validateAliasDto(keystore, aliasDto);
			}
		} catch (GmsException e) {
			throw e;
		} catch (Exception e) {
			log.error("Keystore cannot be loaded!", e);
			throw new GmsException(e, GMS_001);
		}
	}

	public String decrypt(SecretEntity secretEntity) {
		try {
			KeystorePair keyPairData = keystoreDataService.getKeystoreData(secretEntity);
			PrivateKey privateKey = (PrivateKey) keyPairData.getKeystore().getKey(keyPairData.getEntity().getAlias(),
					keyPairData.getEntity().getAliasCredential().toCharArray());

			Cipher decrypt = Cipher.getInstance(privateKey.getAlgorithm());
			decrypt.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] decryptedMessage = decrypt
					.doFinal(Base64.getDecoder().decode(secretEntity.getValue().getBytes(StandardCharsets.UTF_8)));

			return new String(decryptedMessage, StandardCharsets.UTF_8);
		} catch (Exception e) {
			log.warn("Decrypt failed!", e);
			throw new GmsException(e, GMS_001);
		}
	}

	public void encrypt(SecretEntity secretEntity) {
		try {
			KeystorePair keyPairData = keystoreDataService.getKeystoreData(secretEntity);
			String encryptedValue = encrypt(secretEntity.getValue(), keyPairData);
			secretEntity.setValue(encryptedValue);
		} catch (Exception e) {
			log.warn("Encrypt failed!", e);
			throw new GmsException(e, GMS_001);
		}
	}

	public String encrypt(String value, KeystorePair keyPairData)
			throws KeyStoreException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		PublicKey publicKey = keyPairData.getKeystore().getCertificate(keyPairData.getEntity().getAlias()).getPublicKey();

		Cipher encrypt = Cipher.getInstance(publicKey.getAlgorithm());
		encrypt.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encryptedMessage = encrypt.doFinal(value.getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().withoutPadding().encodeToString(encryptedMessage);
	}

	private static void validateAliasDto(KeyStore keystore, KeystoreAliasDto dto)
			throws KeyStoreException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnrecoverableKeyException {

		Certificate cert = keystore.getCertificate(dto.getAlias());
		if (cert == null) {
			throw new GmsException("The given alias("+ dto.getAlias() + ") is not valid!", GMS_006);
		}

		// Encrypt
		PublicKey publicKey = cert.getPublicKey();
		Cipher encrypt = Cipher.getInstance(publicKey.getAlgorithm());
		encrypt.init(Cipher.ENCRYPT_MODE, publicKey);

		byte[] encryptedMessage = encrypt.doFinal("test".getBytes(StandardCharsets.UTF_8));

		// Decrypt
		PrivateKey privateKey = (PrivateKey) keystore.getKey(dto.getAlias(), dto.getAliasCredential().toCharArray());
		Cipher decrypt = Cipher.getInstance(privateKey.getAlgorithm());
		decrypt.init(Cipher.DECRYPT_MODE, privateKey);
		decrypt.doFinal(encryptedMessage);
	}
}
