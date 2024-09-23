package hu.szrnkapeter.gms.sample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hu.szrnkapeter.gms.sample.model.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Optional;
import java.util.Properties;

/**
 * This sample loads a JKS keystore to decrypt the response returned by GMS.
 * 
 * @author Peter Szrnka
 */
@Slf4j
public class LoadSecretWithKeystore {
	
	private static final String URL_SUFFIX = "/api/secret/";
	private static final Gson GSON = new GsonBuilder().create();

	public static void main(String[] args) throws Exception {
		final Properties props = System.getProperties();
		props.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());

		SSLContext sslContext = SSLContext.getInstance("SSL");
		TrustManager[] trustAllCerts = new TrustManager[] {
				new X509TrustManager() {
					@Override
					public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {

					}

					@Override
					public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {

					}

					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return new X509Certificate[0];
					}
				}
		};
		sslContext.init(null, trustAllCerts, new SecureRandom());
		try (HttpClient httpClient = HttpClient.newBuilder().sslContext(sslContext).build()) {

			HttpRequest request = HttpRequest.newBuilder()
					.header("X-API-KEY", System.getenv("API_KEY")).uri(new URI(getBaseUrl() + URL_SUFFIX + System.getenv("SECRET_ID"))).build();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			log.info("Status code: {}", response.statusCode());

			if (response.statusCode() != 200) {
				log.info("Response: {}", response.body());
				return;
			}

			ApiResponseDto responseBody = GSON.fromJson(response.body(), ApiResponseDto.class);
			log.info("Encoded response: {}", responseBody.getValue());
			log.info("Decoded message: {}", DecryptionService.decryptMessage(responseBody.getValue()));
		}
	}

	private static String getBaseUrl() {
		return Optional.ofNullable(System.getenv("GMS_BASE_URL")).orElse("https://localhost:8443");
	}
}