package io.github.gms.common.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.dto.SaveUserRequestDto;
import io.github.gms.secure.service.SystemService;
import io.github.gms.util.TestConstants;
import io.github.gms.util.TestUtils;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Disabled("Temporarily disabled")
@Tag(TestConstants.TAG_INTEGRATION_TEST)
class SetupIntegrationTest extends AbstractIntegrationTest {

	@MockBean
	private SystemService systemService;

	@Override
	@BeforeEach
	public void setup() {
	}

	@Test
	void shouldSetupAdminUser() {
		when(systemService.getSystemStatus()).thenReturn(SystemStatusDto.builder().status("NEED_SETUP").build());

		// act
		SaveUserRequestDto dto = TestUtils.createSaveUserRequestDto();
		HttpEntity<SaveUserRequestDto> requestEntity = new HttpEntity<>(dto);
		ResponseEntity<SaveEntityResponseDto> response = executeHttpPost("/setup/user", requestEntity, SaveEntityResponseDto.class);
		
		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
	}
	
	@Test
	void shouldSetupAdminUserWithoutRoles() {
		when(systemService.getSystemStatus()).thenReturn(SystemStatusDto.builder().status("NEED_SETUP").build());

		// act
		SaveUserRequestDto dto = TestUtils.createSaveUserRequestDtoWithNoRoles("userRandom", UUID.randomUUID().toString() + "@email.com");
		HttpEntity<SaveUserRequestDto> requestEntity = new HttpEntity<>(dto);
		ResponseEntity<SaveEntityResponseDto> response = executeHttpPost("/setup/user", requestEntity, SaveEntityResponseDto.class);
		
		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
	}
}
