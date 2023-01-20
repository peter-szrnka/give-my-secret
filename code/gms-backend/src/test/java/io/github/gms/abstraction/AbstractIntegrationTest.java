package io.github.gms.abstraction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.util.Constants;
import io.github.gms.secure.repository.ApiKeyRepository;
import io.github.gms.secure.repository.KeystoreRepository;
import io.github.gms.secure.repository.SecretRepository;
import io.github.gms.secure.repository.UserRepository;
import io.github.gms.secure.service.JwtService;
import io.github.gms.util.TestUtils;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@ActiveProfiles({ Constants.CONFIG_AUTH_TYPE_DB })
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

	@LocalServerPort
	protected int port;

	@Autowired
	protected RestTemplate rest;

	@Autowired
	protected JwtService jwtService;

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	protected ApiKeyRepository apiKeyRepository;

	@Autowired
	protected KeystoreRepository keystoreRepository;

	@Autowired
	protected SecretRepository secretRepository;

	protected String jwt;
	protected GmsUserDetails gmsUser;
	protected String basePath = "http://localhost:";

	@BeforeEach
	public void setup() {
		gmsUser = TestUtils.createGmsUser();
		jwt = jwtService.generateJwt(TestUtils.createJwtUserRequest());
	}

	/*@AfterAll
	public static void cleanup() {
		File keystoreDirectory = new File("keystores");
		
		if (keystoreDirectory.exists() && keystoreDirectory.isDirectory()) {
			keystoreDirectory.delete();
		}
	}*/

	protected <I, O> ResponseEntity<O> executeHttpGet(String url, HttpEntity<I> requestEntity, Class<O> responseType) {
		return rest.exchange(basePath + port + url, HttpMethod.GET, requestEntity, responseType);
	}

	protected <I, O> ResponseEntity<O> executeHttpPost(String url, HttpEntity<I> requestEntity, Class<O> responseType) {
		return rest.exchange(basePath + port + url, HttpMethod.POST, requestEntity, responseType);
	}

	protected <I, O> ResponseEntity<O> executeHttpDelete(String url, HttpEntity<I> requestEntity,
			Class<O> responseType) {
		return rest.exchange(basePath + port + url, HttpMethod.DELETE, requestEntity, responseType);
	}

	protected <I, O> ResponseEntity<O> executeHttpPut(String url, HttpEntity<I> requestEntity, Class<O> responseType) {
		return rest.exchange(basePath + port + url, HttpMethod.PUT, requestEntity, responseType);
	}
}
