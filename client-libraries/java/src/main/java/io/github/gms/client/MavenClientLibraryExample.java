package io.github.gms.client;

import io.github.gms.client.model.GetSecretRequest;
import io.github.gms.client.model.GetSecretResponse;
import io.github.gms.client.model.GiveMySecretClientConfig;

public class MavenClientLibraryExample {

	public static void main(String[] args) throws Exception {
		GiveMySecretClient client = GiveMySecretClient.create(GiveMySecretClientConfig.builder()
				.withUrl("https://localhost:8443")
				.build());

		// Synchronous sample
		GetSecretResponse response = client.getSecret(GetSecretRequest.builder()
				.withApiKey("yfpcelpTcGCwEwAl0ZnltoGwJfX4FVBw").withSecretId("secret1").build());
		System.out.println("Response = " + response.getValue());
	}
}
