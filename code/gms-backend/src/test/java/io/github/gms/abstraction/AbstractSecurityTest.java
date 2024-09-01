package io.github.gms.abstraction;

import io.github.gms.common.dto.IdNamePairListDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public abstract class AbstractSecurityTest extends AbstractIntegrationTest implements GmsControllerSecurityTest {

	protected String urlPrefix;

	public AbstractSecurityTest(String urlPrefix) {
		this.urlPrefix = "/secure" + urlPrefix;
	}

	@Override
	@BeforeEach
	public void setup() {
		gmsUser = null;
		jwt = null;
	}

	protected <T> void shouldSaveFailWith403(T request) {
		HttpEntity<T> requestEntity = new HttpEntity<>(request, TestUtils.getHttpHeaders(jwt));

		// act
		ResponseEntity<SaveEntityResponseDto> response = executeHttpPost(urlPrefix, requestEntity, SaveEntityResponseDto.class);

		// assert
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}

	protected <T> void shouldGetByIdFailWith403(Class<T> clazz, Long id) {
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

		// act
		ResponseEntity<T> response = executeHttpGet(urlPrefix + "/" + id, requestEntity, clazz);

		// assert
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}

	protected <T> void shouldListFailWith403(Class<T> clazz) {
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

		// act
		ResponseEntity<T> response = executeHttpGet(urlPrefix + "/list?page=0&size=10&direction=ASC&property=id", requestEntity, clazz);

		// assert
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}

	protected void shouldDeleteFailWith403(Long id) {
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

		// act
		ResponseEntity<String> response = executeHttpDelete(urlPrefix + "/" + id, requestEntity, String.class);

		// assert
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}

	protected void shouldToggleFailWith403(Long id) {
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

		// act
		ResponseEntity<String> response = executeHttpPost(urlPrefix + "/" + id + "?enabled=true", requestEntity, String.class);

		// assert
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}

	protected void shouldListingFailWith403(String url) {
		// act
		ResponseEntity<IdNamePairListDto> response =
				executeHttpGet(urlPrefix + url, null, IdNamePairListDto.class);

		// assert
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}
}
