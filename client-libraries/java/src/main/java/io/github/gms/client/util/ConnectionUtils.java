package io.github.gms.client.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.gms.client.model.GetSecretRequest;
import io.github.gms.client.model.GetSecretResponse;
import io.github.gms.client.model.GiveMySecretClientConfig;

/**
 * Connection manager class.
 * 
 * @author Peter Szrnka
 * @since 0.1
 */
public class ConnectionUtils {

	private ConnectionUtils() {
	}

	/**
	 * Returns with the given secret.
	 * 
	 * @param configuration A {@link GiveMySecretClientConfig} object instance.
	 * @return The response with the secret
	 * @throws IOException The unexpected exception
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static GetSecretResponse getResponse(GiveMySecretClientConfig configuration, GetSecretRequest request) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		initHttpsConnection();
		URLConnection connection = initURLConnection(configuration, request);
		InputStream streamResponse = connection.getInputStream();

		try (Scanner scanner = new Scanner(streamResponse)) {
			String responseBody = scanner.useDelimiter("\\A").next();
			return getObjectMapper().readValue(responseBody, GetSecretResponse.class);
		} finally {
			streamResponse.close();
		}
	}

	private static void initHttpsConnection() throws NoSuchAlgorithmException, KeyManagementException {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[0];
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
				// Nothing to do here
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
				// Nothing to do here
			}

		} };

		SSLContext sc = SSLContext.getInstance("TLSv1.2"); 
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		// Create && install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier((hostName, session) -> true);
	}

	private static URLConnection initURLConnection(GiveMySecretClientConfig configuration, GetSecretRequest request) throws IOException {
		URLConnection connection = new URL(configuration.getUrl() + "/api/secret/" + request.getSecretId())
				.openConnection();

		connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.displayName());
		connection.setRequestProperty("x-api-key", request.getApiKey());
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setConnectTimeout(configuration.getDefaultConnectionTimeout());
		connection.setReadTimeout(configuration.getDefaultReadTimeout());
		return connection;
	}

	private static ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
		return objectMapper;
	}
}