package io.github.gms.common.controller;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.abstraction.GmsControllerIntegrationTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.common.enums.SystemStatus;
import io.github.gms.common.enums.UserRole;
import io.github.gms.util.DemoData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static io.github.gms.common.enums.SystemStatus.NEED_SETUP;
import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static io.github.gms.common.util.Constants.SELECTED_AUTH_DB;
import static io.github.gms.util.TestConstants.TAG_INTEGRATION_TEST;
import static io.github.gms.util.TestConstants.URL_INFO_STATUS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_INTEGRATION_TEST)
@TestedClass(InformationController.class)
class InformationControllerIntegrationTest extends AbstractIntegrationTest implements GmsControllerIntegrationTest {

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        when(systemService.getSystemStatus()).thenReturn(SystemStatusDto.builder().withStatus(SystemStatus.OK.name()).build());
    }

    @Test
    @TestedMethod("getVmOptions")
    void getVmOptions_whenCalled_thenReturnVmOptions() {
        when(systemService.getSystemStatus()).thenReturn(SystemStatusDto.builder().withStatus(SystemStatus.NEED_SETUP.name()).build());

        // act
        HttpEntity<Void> requestEntity = new HttpEntity<>(null);
        ResponseEntity<List> response = executeHttpGet("/info/vm_options", requestEntity, List.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(systemService, times(2)).getSystemStatus();
    }

    @Test
    @TestedMethod("getUserInfo")
	void infoMe_whenJwtIsMissing_thenEmptyResponse() {
		// act
		ResponseEntity<UserInfoDto> response = executeHttpGet("/info/me", new HttpEntity<>(null), UserInfoDto.class);
		
		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
	}

    @Test
    @TestedMethod("getUserInfo")
	void infoMe_whenJwtIsValid_thenReturnUserInfo() {
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

    @Test
    @TestedMethod("status")
    void systemStatus_whenSystemIsNotReady_thenReturnNeedSetup() {
        // act
        when(systemService.getSystemStatus()).thenReturn(SystemStatusDto.builder()
                        .withVersion("1.0")
                        .withAuthMode("db")
                .withStatus(SystemStatus.NEED_SETUP.name()).build());
        HttpEntity<Void> requestEntity = new HttpEntity<>(null);
        ResponseEntity<SystemStatusDto> response = executeHttpGet(URL_INFO_STATUS, requestEntity, SystemStatusDto.class);

        // assert
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(NEED_SETUP.name(), response.getBody().getStatus());
        assertNotNull(response.getBody().getVersion());
        assertEquals(SELECTED_AUTH_DB, response.getBody().getAuthMode());
    }

    @Test
    @TestedMethod("getErrorCodes")
    void errorCodes_whenCalled_thenReturnAllErrorCodes() {
        // act
        HttpEntity<Void> requestEntity = new HttpEntity<>(null);
        ResponseEntity<String> response = executeHttpGet("/info/error_codes", requestEntity, String.class);

        // assert
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
