package io.github.gms.client;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import io.github.gms.client.impl.GiveMySecretClientImpl;
import io.github.gms.client.model.GetSecretRequest;
import io.github.gms.client.model.GetSecretResponse;
import io.github.gms.client.model.GiveMySecretClientConfig;

/**
 * Client object for Give My Secret.
 * 
 * @author Peter Szrnka
 * @since 1.0.0
 */
public interface GiveMySecretClient {

	/**
	 * Returns with a secret by the provided input
	 * @param request
	 * @return GetSecretResponse Response object
	 * 
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	GetSecretResponse getSecret(GetSecretRequest request) throws IOException, KeyManagementException, NoSuchAlgorithmException;
	
	static GiveMySecretClient create(GiveMySecretClientConfig config) {
		return new GiveMySecretClientImpl(config);
	}
}