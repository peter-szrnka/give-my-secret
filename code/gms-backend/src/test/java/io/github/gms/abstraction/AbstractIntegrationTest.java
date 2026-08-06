package io.github.gms.abstraction;

import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.common.service.JwtService;
import io.github.gms.functions.apikey.ApiKeyRepository;
import io.github.gms.functions.keystore.KeystoreRepository;
import io.github.gms.functions.secret.SecretRepository;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.user.UserRepository;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_DB;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@ActiveProfiles({ CONFIG_AUTH_TYPE_DB })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

	public final HttpHeaders headers = new HttpHeaders();

	@LocalServerPort
	protected int port;

	@Autowired
	@Qualifier("testRestClient")
	protected RestClient rest;

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

	@MockitoBean
	protected SystemService systemService;

	protected String jwt;
	protected GmsUserDetails gmsUser;
	protected String basePath = "http://localhost:";

    @Autowired
    protected Map<String, Long> entityMap;

    protected Long getEntityId(String domain, Long code) {
        return entityMap.get(domain + "-" + code);
    }

	@BeforeEach
	public void setup() {
		gmsUser = TestUtils.createGmsUser();
		jwt = jwtService.generateJwt(TestUtils.createJwtUserRequest());
		when(systemService.getSystemStatus()).thenReturn(SystemStatusDto.builder()
				.withStatus("OK").withAuthMode("db").withVersion("1.0-TEST")
				.build());
	}

	protected <I, O> ResponseEntity<O> executeHttpGet(String url, HttpEntity<I> requestEntity, Class<O> responseType) {
		addCsrf(requestEntity);
		return rest.get()
				.uri(basePath + port + url)
				.headers(httpHeaders -> httpHeaders.addAll(requestEntity.getHeaders()))
				.retrieve()
				.toEntity(responseType);
	}

	protected <I, O> ResponseEntity<O> executeHttpPost(String url, HttpEntity<I> requestEntity, Class<O> responseType) {
		addCsrf(requestEntity);
        assert requestEntity.getBody() != null;
        return rest.post()
				.uri(basePath + port + url)
				.headers(httpHeaders -> httpHeaders.addAll(requestEntity.getHeaders()))
				.body(requestEntity.getBody())
				.retrieve()
				.toEntity(responseType);
	}

	protected <I, O> ResponseEntity<O> executeHttpDelete(String url, HttpEntity<I> requestEntity,
			Class<O> responseType) {
		addCsrf(requestEntity);
		return rest.delete()
				.uri(basePath + port + url)
				.headers(httpHeaders -> httpHeaders.addAll(requestEntity.getHeaders()))
				.retrieve()
				.toEntity(responseType);
	}

	protected <I> ResponseEntity<String> executeHttpPut(HttpEntity<I> requestEntity) {
		addCsrf(requestEntity);
		assert requestEntity.getBody() != null;
		return rest.put()
				.uri(basePath + port + "/mark_as_read")
				.headers(httpHeaders -> httpHeaders.addAll(requestEntity.getHeaders()))
				.body(requestEntity.getBody())
				.retrieve()
				.toEntity(String.class);
	}

	protected static <I> void addCsrf(HttpEntity<I> requestEntity) {
		if (requestEntity == null) {
			return;
		}

		HttpHeaders headers = new HttpHeaders(requestEntity.getHeaders());
		headers.add("X-XSRF-TOKEN","1");
		headers.add("Cookie", "XSRF-TOKEN=1");
	}
}
