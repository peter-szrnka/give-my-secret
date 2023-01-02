package io.github.gms.common.db.converter;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
public class EncryptedFieldConverter implements AttributeConverter<String, String> {

	// TODO Expose this parameter as an external property
	private static final String ALGORYTHM = "AES";

	@Value("${config.crypto.secret}")
	private String secret;

	private Key key;
	private final Cipher cipher;

	public EncryptedFieldConverter() throws NoSuchAlgorithmException, NoSuchPaddingException {
        cipher = Cipher.getInstance(ALGORYTHM);
    }

	@Override
	public String convertToDatabaseColumn(String attribute) {
		try {
			key = new SecretKeySpec(Base64.getDecoder().decode(secret.getBytes()), ALGORYTHM);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
		} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String convertToEntityAttribute(String dbData) {
		try {
			key = new SecretKeySpec(Base64.getDecoder().decode(secret.getBytes()), ALGORYTHM);
			cipher.init(Cipher.DECRYPT_MODE, key);
			return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
		} catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
			throw new IllegalStateException(e);
		}
	}
}
