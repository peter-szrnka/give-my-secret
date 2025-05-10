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

import static io.github.gms.util.TestConstants.*;
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
	@TestedMethod(SAVE)
	void save_whenAuthenticationFails_thenReturnHttp403() {
		assertSaveFailWith403(TestUtils.createSaveApiKeyRequestDto());
	}
	
	@Test
	@TestedMethod(GET_BY_ID)
	void getById_whenAuthenticationFails_thenReturnHttp403() {
		assertGetByIdFailWith403(ApiKeyDto.class, DemoData.API_KEY_1_ID);
	}
	
	@Test
	@TestedMethod(LIST)
	void list_whenAuthenticationFails_thenReturnHttp403() {
		assertListFailWith403(ApiKeyListDto.class);
	}
	
	@Test
	@TestedMethod(GET_VALUE)
	void getValue_whenAuthenticationFails_thenReturnHttp403() {
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		addCsrf(requestEntity);

		// act
		ResponseEntity<String> response = executeHttpGet(urlPrefix + "/value/" + DemoData.API_KEY_1_ID, requestEntity, String.class);

		// assert
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}
	
	@Test
	@TestedMethod(DELETE)
	void delete_whenAuthenticationFails_thenReturnHttp403() {
		assertDeleteFailWith403(DemoData.API_KEY_2_ID);
	}
	
	@Test
	@TestedMethod(TOGGLE)
	void toggleStatus_whenAuthenticationFails_thenReturnHttp403() {
		assertToggleFailWith403(DemoData.API_KEY_1_ID);
	}

	@Test
	@TestedMethod("getAllApiKeyNames")
	void listAllApiKeyNames_whenAuthenticationFails_thenReturnHttp403() {
		assertListingFailWith403("/secure/apikey/list_names");
	}
}
