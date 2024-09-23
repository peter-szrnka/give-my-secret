package hu.szrnkapeter.gms.sample;

import io.github.gms.client.GiveMySecretClient;
import io.github.gms.client.builder.GiveMySecretClientBuilder;
import io.github.gms.client.enums.KeystoreType;
import io.github.gms.client.model.GetSecretRequest;
import io.github.gms.client.model.GiveMySecretClientConfig;

import java.io.FileInputStream;
import java.util.Map;
import java.util.Optional;

public class MavenClientLibraryExample {

	public static void main(String[] args) throws Exception {
		GiveMySecretClient client = GiveMySecretClientBuilder.create(GiveMySecretClientConfig.builder()
				.url(getInstanceUrl())
				.build());

		Map<String, String> response = client.getSecret(GetSecretRequest.builder()
						.keystore(new FileInputStream("src/main/resources/test.p12"))
						.keystoreCredential("test")
						.keystoreAlias("test")
						.keystoreAliasCredential("test")
						.keystoreType(KeystoreType.PKCS12)
				.apiKey(System.getenv("API_KEY"))
				.secretId(System.getenv("SECRET_ID"))
				.build());

		System.out.println("Response = " + response.get("value"));
	}

	private static String getInstanceUrl() {
		return Optional.ofNullable(System.getenv("GMS_URL")).orElse("https://localhost:8443");
	}
}
