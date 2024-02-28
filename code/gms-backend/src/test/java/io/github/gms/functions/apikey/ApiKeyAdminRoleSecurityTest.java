package io.github.gms.functions.apikey;

import io.github.gms.abstraction.AbstractAdminRoleSecurityTest;
import io.github.gms.functions.apikey.ApiKeyDto;
import io.github.gms.functions.apikey.ApiKeyListDto;
import io.github.gms.common.dto.IdNamePairListDto;
import io.github.gms.common.dto.PagingDto;
import io.github.gms.functions.apikey.SaveApiKeyRequestDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.HttpClientErrorException;

import static io.github.gms.util.TestConstants.TAG_SECURITY_TEST;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_SECURITY_TEST)
class ApiKeyAdminRoleSecurityTest extends AbstractAdminRoleSecurityTest {

	@Test
	void testSaveFailWithHttp403() {
		HttpEntity<SaveApiKeyRequestDto> requestEntity = new HttpEntity<>(TestUtils.createSaveApiKeyRequestDto(), TestUtils.getHttpHeaders(jwt));

		// assert
		HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
			executeHttpPost("/secure/apikey", requestEntity, SaveEntityResponseDto.class));
		
		assertTrue(exception.getMessage().startsWith("403"));
	}
	
	@Test
	void testGetByIdFailWithHttp403() {
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

		// assert
		HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
		executeHttpGet("/secure/apikey/" + DemoData.API_KEY_1_ID, requestEntity, ApiKeyDto.class));
		
		assertTrue(exception.getMessage().startsWith("403"));
	}
	
	@Test
	void testListFailWithHttp403() {
		PagingDto request = PagingDto.builder().page(0).size(50).direction("ASC").property("id").build();
		HttpEntity<PagingDto> requestEntity = new HttpEntity<>(request, TestUtils.getHttpHeaders(jwt));

		// assert
		HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
		executeHttpPost("/secure/apikey/list", requestEntity, ApiKeyListDto.class));
		
		assertTrue(exception.getMessage().startsWith("403"));
	}
	
	@Test
	void testGetValueFailWithHttp403() {
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

		// assert
		HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
		executeHttpGet("/secure/apikey/value/" + DemoData.API_KEY_1_ID, requestEntity, String.class));
		
		assertTrue(exception.getMessage().startsWith("403"));
	}
	
	@Test
	void testDeleteFailWithHttp403() {
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

		// assert
		HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
			executeHttpDelete("/secure/apikey/" + DemoData.API_KEY_2_ID, requestEntity,
				String.class));
		
		assertTrue(exception.getMessage().startsWith("403"));
	}
	
	@Test
	void testToggleStatusFailWithHttp403() {
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

		// assert
		HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
		executeHttpPost("/secure/apikey/" + DemoData.API_KEY_1_ID + "?enabled=true", requestEntity,
				String.class));

		assertTrue(exception.getMessage().startsWith("403"));
	}

	@Test
	void testListAllApiKeyNamesFailWithHttp403() {
		// assert
		HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
				executeHttpGet("/secure/apikey/list_names", null, IdNamePairListDto.class));

		assertTrue(exception.getMessage().startsWith("403"));
	}
}
