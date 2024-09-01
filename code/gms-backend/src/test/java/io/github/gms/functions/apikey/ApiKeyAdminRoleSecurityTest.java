package io.github.gms.functions.apikey;

import io.github.gms.abstraction.AbstractAdminRoleSecurityTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static io.github.gms.util.TestConstants.TAG_SECURITY_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_SECURITY_TEST)
@TestedClass(ApiKeyController.class)
class ApiKeyAdminRoleSecurityTest extends AbstractAdminRoleSecurityTest {

	public ApiKeyAdminRoleSecurityTest() {
		super("/apikey");
	}

	@Test
	@TestedMethod("save")
	void testSaveFailWithHttp403() {
		shouldSaveFailWith403(TestUtils.createSaveApiKeyRequestDto());
	}
	
	@Test
	@TestedMethod("getById")
	void testGetByIdFailWithHttp403() {
		shouldGetByIdFailWith403(ApiKeyDto.class, DemoData.API_KEY_1_ID);
	}
	
	@Test
	@TestedMethod("list")
	void testListFailWithHttp403() {
		shouldListFailWith403(ApiKeyListDto.class);
	}
	
	@Test
	@TestedMethod("getValue")
	void testGetValueFailWithHttp403() {
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

		// act
		ResponseEntity<String> response = executeHttpGet(urlPrefix + "/value/" + DemoData.API_KEY_1_ID, requestEntity, String.class);

		// assert
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}
	
	@Test
	@TestedMethod("delete")
	void testDeleteFailWithHttp403() {
		shouldDeleteFailWith403(DemoData.API_KEY_2_ID);
	}
	
	@Test
	@TestedMethod("toggle")
	void testToggleStatusFailWithHttp403() {
		shouldToggleFailWith403(DemoData.API_KEY_1_ID);
	}

	@Test
	@TestedMethod("getAllApiKeyNames")
	void testListAllApiKeyNamesFailWithHttp403() {
		shouldListingFailWith403("/secure/apikey/list_names");
	}
}
