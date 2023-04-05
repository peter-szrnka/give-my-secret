package hu.szrnkapeter.gms.sample;

import io.github.gms.client.GiveMySecretClient;
import io.github.gms.client.builder.GiveMySecretClientBuilder;
import io.github.gms.client.enums.KeystoreType;
import io.github.gms.client.model.GetSecretRequest;
import io.github.gms.client.model.GiveMySecretClientConfig;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

public class MavenClientLibraryExample {

	public static void main(String[] args) throws Exception {
		GiveMySecretClient client = GiveMySecretClientBuilder.create(GiveMySecretClientConfig.builder()
				.withUrl("https://localhost:8443")
				.build());

		Map<String, String> response = client.getSecret(GetSecretRequest.builder()
						.withKeystore(new FileInputStream(new File("src/main/resources/test.p12")))
						.withKeystoreCredential("test")
						.withKeystoreAlias("test")
						.withKeystoreAliasCredential("test")
						.withKeystoreType(KeystoreType.PKCS12)
				.withApiKey("<api_key>").withSecretId("secret2").build());
		System.out.println("Response = " + response.toString());
	}
}
