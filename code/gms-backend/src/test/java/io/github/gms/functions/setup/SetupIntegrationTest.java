package io.github.gms.functions.setup;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.abstraction.GmsControllerIntegrationTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.dto.SimpleResponseDto;
import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.common.enums.SystemStatus;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.user.SaveUserRequestDto;
import io.github.gms.functions.user.UserDto;
import io.github.gms.functions.user.UserService;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static io.github.gms.util.TestConstants.TAG_INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_INTEGRATION_TEST)
@TestedClass(SetupController.class)
class SetupIntegrationTest extends AbstractIntegrationTest implements GmsControllerIntegrationTest {

	@MockBean
	private SystemService systemService;

	@MockBean
	private UserService userService;

	@Test
	@TestedMethod("stepBack")
	void stepBack_whenCalled_thenReturnStepBack() {
		when(systemService.getSystemStatus()).thenReturn(SystemStatusDto.builder().withStatus(SystemStatus.NEED_ADMIN_USER.name()).build());

		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(null);
		ResponseEntity<String> response = executeHttpGet("/setup/step_back", requestEntity, String.class);

		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		verify(systemService, times(2)).getSystemStatus();
	}

	@Test
	@TestedMethod("getCurrentSuperAdmin")
	void getCurrentSuperAdmin_whenCalled_thenReturnCurrentSuperAdmin() {
		when(systemService.getSystemStatus()).thenReturn(SystemStatusDto.builder().withStatus(SystemStatus.NEED_AUTH_CONFIG.name()).build());
		when(userService.getById(1L)).thenReturn(TestUtils.createUserDto());

		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(null);
		ResponseEntity<UserDto> response = executeHttpGet("/setup/current_super_admin", requestEntity, UserDto.class);

		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		verify(systemService, times(2)).getSystemStatus();
		verify(userService).getById(1L);
	}

	@Test
	@TestedMethod("saveInitialStep")
	void saveInitialStep_whenCalled_thenReturnSaveInitialStep() {
		when(systemService.getSystemStatus()).thenReturn(SystemStatusDto.builder().withStatus(SystemStatus.NEED_SETUP.name()).build());

		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(null);
		ResponseEntity<SaveEntityResponseDto> response = executeHttpPost("/setup/initial", requestEntity, SaveEntityResponseDto.class);

		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		verify(systemService, times(2)).getSystemStatus();
	}

	@Test
	@TestedMethod("saveAdminUser")
	void saveAdminUser_whenUserIsASimpleUser_thenSaveUser() {
		when(systemService.getSystemStatus()).thenReturn(SystemStatusDto.builder().withStatus(SystemStatus.NEED_SETUP.name()).build());
		when(userService.saveAdminUser(any(SaveUserRequestDto.class))).thenReturn(TestUtils.createSaveEntityResponseDto(1L));

		// act
		SaveUserRequestDto dto = TestUtils.createSaveUserRequestDto();
		HttpEntity<SaveUserRequestDto> requestEntity = new HttpEntity<>(dto);
		ResponseEntity<SaveEntityResponseDto> response = executeHttpPost("/setup/user", requestEntity, SaveEntityResponseDto.class);
		
		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
	}
	
	@Test
	void saveAdminUser_whenUserIsAdminWithoutRoles_thenSaveUser() {
		when(systemService.getSystemStatus()).thenReturn(SystemStatusDto.builder().withStatus(SystemStatus.NEED_SETUP.name()).build());
		SaveUserRequestDto dto = TestUtils.createSaveUserRequestDtoWithNoRoles("userRandom", UUID.randomUUID() + "@email.com");
		when(userService.saveAdminUser(dto)).thenReturn(TestUtils.createSaveEntityResponseDto(1L));

		// act
		HttpEntity<SaveUserRequestDto> requestEntity = new HttpEntity<>(dto);
		ResponseEntity<SaveEntityResponseDto> response = executeHttpPost("/setup/user", requestEntity, SaveEntityResponseDto.class);
		
		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@Test
	@TestedMethod("saveSystemProperties")
	void saveSystemProperties_whenCalled_thenReturnSaveSystemProperties() {
		when(systemService.getSystemStatus()).thenReturn(SystemStatusDto.builder().withStatus(SystemStatus.NEED_SETUP.name()).build());
		SetupSystemPropertiesDto requestDto = new SetupSystemPropertiesDto();
		requestDto.setProperties(List.of());

		// act
		HttpEntity<SetupSystemPropertiesDto> requestEntity = new HttpEntity<>(requestDto);
		ResponseEntity<SimpleResponseDto> response = executeHttpPost("/setup/properties", requestEntity, SimpleResponseDto.class);

		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		verify(systemService, times(2)).getSystemStatus();
	}

	@Test
	@TestedMethod("saveOrganizationData")
	void saveOrganizationData_whenCalled_thenReturnSaveOrganizationData() {
		when(systemService.getSystemStatus()).thenReturn(SystemStatusDto.builder().withStatus(SystemStatus.NEED_SETUP.name()).build());
		SetupSystemPropertiesDto requestDto = new SetupSystemPropertiesDto();
		requestDto.setProperties(List.of());

		// act
		HttpEntity<SetupSystemPropertiesDto> requestEntity = new HttpEntity<>(requestDto);
		ResponseEntity<SimpleResponseDto> response = executeHttpPost("/setup/org_data", requestEntity, SimpleResponseDto.class);

		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		verify(systemService, times(2)).getSystemStatus();
	}

	@Test
	@TestedMethod("completeSetup")
	void completeSetup_whenCalled_thenReturnCompleteSetup() {
		when(systemService.getSystemStatus()).thenReturn(SystemStatusDto.builder().withStatus(SystemStatus.COMPLETE.name()).build());

		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(null);
		ResponseEntity<SimpleResponseDto> response = executeHttpPost("/setup/complete", requestEntity, SimpleResponseDto.class);

		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		verify(systemService, times(2)).getSystemStatus();
	}
}
