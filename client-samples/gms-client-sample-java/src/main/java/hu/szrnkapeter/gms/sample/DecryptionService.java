package hu.szrnkapeter.gms.sample;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class DecryptionService {

	public static String decryptMessage(String input) 
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException,
			IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {
		KeyStore ks = loadKeystore("test2.jks");
		PrivateKey pk = (PrivateKey) ks.getKey("test", "test".toCharArray());

		Cipher decrypt = Cipher.getInstance(pk.getAlgorithm());
		decrypt.init(Cipher.DECRYPT_MODE, pk);
		byte[] decryptedMessage = decrypt.doFinal(Base64.getDecoder().decode(input.getBytes(StandardCharsets.UTF_8)));

		return new String(decryptedMessage, StandardCharsets.UTF_8);
	}

	private static KeyStore loadKeystore(String filePath) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		File keystoreFile = new File("src/main/resources/" + filePath);
		KeyStore keystore = KeyStore.getInstance("JKS");
		keystore.load(new ByteArrayInputStream(Files.readAllBytes(keystoreFile.toPath())), "test".toCharArray());

		return keystore;
	}
}