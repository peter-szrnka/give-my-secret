package io.github.gms.controller.security;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.HttpClientErrorException;

import io.github.gms.abstraction.AbstractSecurityTest;
import io.github.gms.secure.dto.SaveApiKeyRequestDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.util.TestConstants;
import io.github.gms.util.TestUtils;

/**
 * Security test of Events
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TestConstants.TAG_SECURITY_TEST)
class EventAdminRoleSecurityTest extends AbstractSecurityTest {

	@Test
	void testListFailWithHttp403() {
		HttpEntity<SaveApiKeyRequestDto> requestEntity = new HttpEntity<>(TestUtils.createSaveApiKeyRequestDto(), TestUtils.getHttpHeaders(jwt));

		// assert
		assertThrows(HttpClientErrorException.Forbidden.class, () ->
			executeHttpPost("/secure/event/list", requestEntity, SaveEntityResponseDto.class));
	}

}
