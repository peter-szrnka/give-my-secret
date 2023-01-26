package io.github.gms.client.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.util.Base64;

import javax.crypto.Cipher;

import io.github.gms.client.GiveMySecretClient;
import io.github.gms.client.model.GetSecretRequest;
import io.github.gms.client.model.GetSecretResponse;
import io.github.gms.client.model.GiveMySecretClientConfig;
import io.github.gms.client.util.ConnectionUtils;

/**
 * Default implementation for client.
 * 
 * @author Peter Szrnka
 * @since 1.0.0
 */
public class GiveMySecretClientImpl implements GiveMySecretClient {

	private GiveMySecretClientConfig configuration;

	public GiveMySecretClientImpl(GiveMySecretClientConfig configuration) {
		if (configuration == null) {
			throw new IllegalArgumentException("Configration is mandatory!");
		}

		this.configuration = configuration;
	}

	@Override
	public GetSecretResponse getSecret(GetSecretRequest request)
			throws IOException, KeyManagementException, NoSuchAlgorithmException {
		validateConfig(request);
		GetSecretResponse response = ConnectionUtils.getResponse(configuration, request);
		
		if (request.getKeystore() != null) {
			return decryptWithKeystore(request, response);
		}

		return response;
	}

	private GetSecretResponse decryptWithKeystore(GetSecretRequest request, GetSecretResponse response) {
		KeyStore ks;
		try {
			ks = loadKeystore(request);
			
			PrivateKey pk = (PrivateKey) ks.getKey(request.getKeystoreAlias(), request.getKeystoreAliasCredential().toCharArray());

			Cipher decrypt = Cipher.getInstance(pk.getAlgorithm());
			decrypt.init(Cipher.DECRYPT_MODE, pk);
			byte[] decryptedMessage = decrypt.doFinal(Base64.getDecoder().decode(response.getValue().getBytes(StandardCharsets.UTF_8)));
			
			return GetSecretResponse.builder().withValue(new String(decryptedMessage, StandardCharsets.UTF_8)).build();
		} catch (Exception e) {
			throw new RuntimeException("Message cannot be decrypted!", e);
		}
	}
	
	private static KeyStore loadKeystore(GetSecretRequest request) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore keystore = KeyStore.getInstance(request.getKeystoreType().getType());
		keystore.load(request.getKeystore(), request.getKeystoreCredential().toCharArray());

		return keystore;
	}

	private void validateConfig(GetSecretRequest request) {
		if (request.getApiKey() == null) {
			throw new IllegalArgumentException("API key is mandatory!");
		}

		if (request.getSecretId() == null) {
			throw new IllegalArgumentException("Secret Id is mandatory!");
		}

		if (request.getKeystore() != null && isKeystoreConfigNotValid(request)) {
			throw new IllegalArgumentException(
					"Invalid configuration: All keystore parameter must be set if keystore stream defined!");
		}
	}

	private static boolean isKeystoreConfigNotValid(GetSecretRequest request) {
		return request.getKeystoreType() == null || request.getKeystoreCredential() == null || request.getKeystoreAlias() == null
				|| request.getKeystoreAliasCredential() == null;
	}
}