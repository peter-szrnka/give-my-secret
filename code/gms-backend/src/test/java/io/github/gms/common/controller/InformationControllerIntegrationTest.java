package io.github.gms.common.controller;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.abstraction.GmsControllerIntegrationTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.common.enums.UserRole;
import io.github.gms.util.DemoData;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static io.github.gms.util.TestConstants.TAG_INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_INTEGRATION_TEST)
@TestedClass(value = InformationController.class, skip = true) // TODO Fix test related issues
class InformationControllerIntegrationTest extends AbstractIntegrationTest implements GmsControllerIntegrationTest {

    @Test
    @TestedMethod("getUserInfo")
	void shouldReturnHttp200WithEmptyResponse() {
		// act
		ResponseEntity<UserInfoDto> response = executeHttpGet("/info/me", new HttpEntity<>(null), UserInfoDto.class);
		
		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
	}

    @Test
    @TestedMethod("getUserInfo")
	void shouldReturnHttp200() {
        // arrange
		HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", ACCESS_JWT_TOKEN + "=" + jwt);

        // act
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
		ResponseEntity<UserInfoDto> response = executeHttpGet("/info/me", requestEntity, UserInfoDto.class);

		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());

        UserInfoDto result = response.getBody();
        assertNotNull(result);
        assertEquals(DemoData.USER_1_ID, result.getId());
        assertEquals("a@b.hu", result.getEmail());
        assertEquals(DemoData.USERNAME1, result.getName());
        assertEquals(DemoData.USERNAME1, result.getUsername());
        assertEquals(UserRole.ROLE_USER, result.getRole());
	}
}
