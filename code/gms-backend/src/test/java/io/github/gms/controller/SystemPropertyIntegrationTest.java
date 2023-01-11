package io.github.gms.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SystemPropertyDto;
import io.github.gms.secure.dto.SystemPropertyListDto;
import io.github.gms.util.TestConstants;
import io.github.gms.util.TestUtils;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TestConstants.TAG_INTEGRATION_TEST)
class SystemPropertyIntegrationTest extends AbstractIntegrationTest {
	
	@Override
	@BeforeEach
	public void setup() {
		gmsUser = TestUtils.createGmsAdminUser();
		jwt = jwtService.generateJwt(gmsUser);
	}
	
	@Test
	void testSave() {
		// act
		HttpEntity<SystemPropertyDto> requestEntity = new HttpEntity<>(SystemPropertyDto.builder().key(SystemProperty.OLD_EVENT_TIME_LIMIT_DAYS.name()).value("2").build(), TestUtils.getHttpHeaders(jwt));
		ResponseEntity<Void> response = executeHttpPost("/secure/system_property", requestEntity, Void.class);
		
		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
	}
	
	@Test
	void testDelete() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpDelete("/secure/system_property/" + SystemProperty.REFRESH_JWT_ALGORITHM.name(), requestEntity, String.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Test
	void testList() {
		// act
		PagingDto request = PagingDto.builder().page(0).size(50).direction("ASC").property("id").build();

		HttpEntity<PagingDto> requestEntity = new HttpEntity<>(request, TestUtils.getHttpHeaders(jwt));
		ResponseEntity<SystemPropertyListDto> response = executeHttpPost("/secure/system_property/list", requestEntity, SystemPropertyListDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		SystemPropertyListDto responseList = response.getBody();
		assertEquals(5, responseList.getResultList().size());
	}
}