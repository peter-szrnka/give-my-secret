package hu.szrnkapeter.gms.sample;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import hu.szrnkapeter.gms.sample.model.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;

/**
 * This sample loads a JKS keystore to decrypt the response returned by GMS.
 * 
 * @author Peter Szrnka
 */
@Slf4j
public class LoadSecretWithKeystore {
	
	private static final String URL = "http://localhost:8080/api/secret/";
	private static final String AP_KEY = "IntTestApiKey";
	private static final String SECRET_ID = "TestSecret1";
	private static final Gson GSON = new GsonBuilder().create();

	public static void main(String[] args) throws Exception {
		HttpClient httpClient = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder()
		  .header("X-API-KEY", AP_KEY).uri(new URI(URL + SECRET_ID)).build();
		
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