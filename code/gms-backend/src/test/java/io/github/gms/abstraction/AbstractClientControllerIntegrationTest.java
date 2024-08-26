package io.github.gms.abstraction;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public abstract class AbstractClientControllerIntegrationTest extends AbstractIntegrationTest implements GmsControllerIntegrationTest {

	protected final String path;

	protected AbstractClientControllerIntegrationTest(String path) {
		this.path = path;
	}
	
	@Override
	protected <I,O> ResponseEntity<O> executeHttpGet(String url, HttpEntity<I> requestEntity, Class<O> responseType) {
		return rest.exchange(basePath + port + path + url, HttpMethod.GET, requestEntity, responseType);
	}
	
	@Override
	protected <I,O> ResponseEntity<O> executeHttpPost(String url, HttpEntity<I> requestEntity, Class<O> responseType) {
		return rest.exchange(basePath + port + path + url, HttpMethod.POST, requestEntity, responseType);
	}
	
	@Override
	protected <I, O> ResponseEntity<O> executeHttpDelete(String url, HttpEntity<I> requestEntity,
			Class<O> responseType) {
		return rest.exchange(basePath + port + path + url, HttpMethod.DELETE, requestEntity, responseType);
	}
	
	@Override
	protected <I> ResponseEntity<String> executeHttpPut(HttpEntity<I> requestEntity) {
		return rest.exchange(basePath + port + path + "/mark_as_read", HttpMethod.PUT, requestEntity, String.class);
	}
}
