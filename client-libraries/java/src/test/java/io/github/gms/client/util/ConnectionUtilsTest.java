package io.github.gms.client.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import io.github.gms.client.model.GetSecretRequest;
import io.github.gms.client.model.GetSecretResponse;
import io.github.gms.client.model.GiveMySecretClientConfig;
import lombok.SneakyThrows;

/**
Unit test of {@link ConnectionUtils}

 * @author Peter Szrnka
 * @since 0.1
 */
@WireMockTest(httpsPort = 9443, httpsEnabled = true)
class ConnectionUtilsTest {

	@Test
	@SneakyThrows
	void test() {
		// arrange
		WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/api/secret/secret1"))
	            .willReturn(WireMock.aResponse()
	                .withHeader("Content-Type", "application/json")
	                .withBody("{ \"value\" : \"my-value\"}")));
		
		// act
		GiveMySecretClientConfig configuration = GiveMySecretClientConfig.builder()
				.withUrl("https://localhost:9443")
				.withDefaultConnectionTimeout(60000)
				.withDefaultReadTimeout(60000)
				.build();
		GetSecretRequest request = GetSecretRequest.builder().withApiKey("api-key").withSecretId("secret1").build();

		GetSecretResponse response = ConnectionUtils.getResponse(configuration, request);
		assertNotNull(response);
		assertEquals("my-value", response.getValue());
	}
}