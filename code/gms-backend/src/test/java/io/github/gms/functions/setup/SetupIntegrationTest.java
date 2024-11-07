package io.github.gms.functions.setup;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.abstraction.GmsControllerIntegrationTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.user.SaveUserRequestDto;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static io.github.gms.util.TestConstants.TAG_INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_INTEGRATION_TEST)
@TestedClass(SetupController.class)
class SetupIntegrationTest extends AbstractIntegrationTest implements GmsControllerIntegrationTest {

	@MockBean
	private SystemService systemService;

	@Test
	@TestedMethod("saveAdminUser")
	void shouldSetupAdminUser() {
		when(systemService.getSystemStatus()).thenReturn(SystemStatusDto.builder().withStatus("NEED_SETUP").build());

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
		when(systemService.getSystemStatus()).thenReturn(SystemStatusDto.builder().withStatus("NEED_SETUP").build());

		// act
		SaveUserRequestDto dto = TestUtils.createSaveUserRequestDtoWithNoRoles("userRandom", UUID.randomUUID() + "@email.com");
		HttpEntity<SaveUserRequestDto> requestEntity = new HttpEntity<>(dto);
		ResponseEntity<SaveEntityResponseDto> response = executeHttpPost("/setup/user", requestEntity, SaveEntityResponseDto.class);
		
		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
	}
}
