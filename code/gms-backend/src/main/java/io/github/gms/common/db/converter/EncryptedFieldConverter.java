package io.github.gms.common.db.converter;

import jakarta.persistence.AttributeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
@ConditionalOnProperty(value = "config.encryption.enable", havingValue = "true", matchIfMissing = true)
public class EncryptedFieldConverter implements AttributeConverter<String, String> {

	private static final int AUTHENTICATION_TAG_LENGTH = 128;
	private static final String ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding";
	private static final String AES = "AES";

	private final String secret;
	private final String encryptionIv;

	public EncryptedFieldConverter(
			@Value("${config.crypto.secret}") String secret, 
			@Value("${config.encryption.iv}") String encryptionIv) {
        this.secret = secret;
        this.encryptionIv = encryptionIv;
    }

	@Override
	public String convertToDatabaseColumn(String attribute) {
		try {
			Key key = new SecretKeySpec(Base64.getDecoder().decode(secret.getBytes()), AES);
			Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(AUTHENTICATION_TAG_LENGTH, encryptionIv.getBytes(StandardCharsets.UTF_8)));
			return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
		} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String convertToEntityAttribute(String dbData) {
		try {
			Key key = new SecretKeySpec(Base64.getDecoder().decode(secret.getBytes()), AES);
			Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(AUTHENTICATION_TAG_LENGTH, encryptionIv.getBytes(StandardCharsets.UTF_8)));
			return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
		} catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new IllegalStateException(e);
		}
	}
}
