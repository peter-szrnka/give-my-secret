package io.github.gms.abstraction;

import org.springframework.http.HttpEntity;
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
		addCsrf(requestEntity);
		return rest.get()
				.uri(basePath + port + path + url)
				.headers(httpHeaders -> httpHeaders.addAll(requestEntity.getHeaders()))
				.retrieve()
				.toEntity(responseType);
	}
	
	@Override
	protected <I,O> ResponseEntity<O> executeHttpPost(String url, HttpEntity<I> requestEntity, Class<O> responseType) {
		addCsrf(requestEntity);
		assert requestEntity.getBody() != null;
		return rest.post()
				.uri(basePath + port + path + url)
				.headers(httpHeaders -> httpHeaders.addAll(requestEntity.getHeaders()))
				.body(requestEntity.getBody())
				.retrieve()
				.toEntity(responseType);
	}
	
	@Override
	protected <I, O> ResponseEntity<O> executeHttpDelete(String url, HttpEntity<I> requestEntity,
			Class<O> responseType) {
		addCsrf(requestEntity);
		return rest.delete()
				.uri(basePath + port + path + url)
				.headers(httpHeaders -> httpHeaders.addAll(requestEntity.getHeaders()))
				.retrieve()
				.toEntity(responseType);
	}
	
	@Override
	protected <I> ResponseEntity<String> executeHttpPut(HttpEntity<I> requestEntity) {
		addCsrf(requestEntity);
		assert requestEntity.getBody() != null;
		return rest.put()
				.uri(basePath + port + path + "/mark_as_read")
				.headers(httpHeaders -> httpHeaders.addAll(requestEntity.getHeaders()))
				.body(requestEntity.getBody())
				.retrieve()
				.toEntity(String.class);
	}
}
