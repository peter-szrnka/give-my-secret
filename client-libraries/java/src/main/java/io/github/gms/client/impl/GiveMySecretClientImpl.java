package io.github.gms.client.impl;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import io.github.gms.client.GiveMySecretClient;
import io.github.gms.client.model.GetSecretRequest;
import io.github.gms.client.model.GetSecretResponse;
import io.github.gms.client.model.GiveMySecretClientConfig;
import io.github.gms.client.util.ConnectionUtils;

/**
 * Default implementation for client.
 * 
 * @author Peter Szrnka
 * @since 1.0
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
		// TODO Process with keystore
		return response;
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
		return request.getKeystoreCredential() == null || request.getKeystoreAlias() == null
				|| request.getKeystoreAliasCredential() == null;
	}
}