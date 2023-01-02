package io.github.gms.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.github.gms.abstraction.AbstractClientControllerIntegrationTest;
import io.github.gms.common.util.DemoDataProviderService;
import io.github.gms.secure.dto.AnnouncementDto;
import io.github.gms.secure.dto.AnnouncementListDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveAnnouncementDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.util.TestConstants;
import io.github.gms.util.TestUtils;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TestConstants.TAG_INTEGRATION_TEST)
class AnnouncementIntegrationTest extends AbstractClientControllerIntegrationTest {

	protected AnnouncementIntegrationTest() {
		super("/secure/announcement");
	}
	
	@Override
	@BeforeEach
	public void setup() {
		gmsUser = TestUtils.createGmsAdminUser();
		jwt = jwtService.generateJwt(gmsUser);
	}

	@Test
	void testSave() {
		// act
		HttpEntity<SaveAnnouncementDto> requestEntity = new HttpEntity<>(TestUtils.createSaveAnnouncementDto(), TestUtils.getHttpHeaders(jwt));
		ResponseEntity<SaveEntityResponseDto> response = executeHttpPost("", requestEntity, SaveEntityResponseDto.class);
		
		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		SaveEntityResponseDto responseBody = response.getBody();
		assertTrue(responseBody.isSuccess());
	}
	
	@Test
	void testGetById() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<AnnouncementDto> response = executeHttpGet("/" + DemoDataProviderService.ANNOUNCEMENT_ID, requestEntity, AnnouncementDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		AnnouncementDto responseBody = response.getBody();
		assertEquals(DemoDataProviderService.ANNOUNCEMENT_ID, responseBody.getId());
		assertEquals("title", responseBody.getTitle());
	}
	
	@Test
	void testList() {
		// act
		PagingDto request = PagingDto.builder().page(0).size(50).direction("ASC").property("id").build();

		HttpEntity<PagingDto> requestEntity = new HttpEntity<>(request, TestUtils.getHttpHeaders(jwt));
		ResponseEntity<AnnouncementListDto> response = executeHttpPost("/list", requestEntity, AnnouncementListDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		AnnouncementListDto responseList = response.getBody();
		assertEquals(1, responseList.getResultList().size());
	}
	
	@Test
	void testDelete() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpDelete("/" + 2, requestEntity,
				String.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
	}
}
