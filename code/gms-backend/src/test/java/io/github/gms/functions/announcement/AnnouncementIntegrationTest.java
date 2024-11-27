package io.github.gms.functions.announcement;

import io.github.gms.abstraction.AbstractClientControllerIntegrationTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static io.github.gms.util.TestConstants.TAG_INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_INTEGRATION_TEST)
@TestedClass(AnnouncementController.class)
class AnnouncementIntegrationTest extends AbstractClientControllerIntegrationTest {

	protected AnnouncementIntegrationTest() {
		super("/secure/announcement");
	}
	
	@Override
	@BeforeEach
	public void setup() {
		gmsUser = TestUtils.createGmsAdminUser();
		jwt = jwtService.generateJwt(TestUtils.createJwtUserRequest(gmsUser));
	}

	@Test
	@TestedMethod("save")
	void save_whenInputIsValid_thenReturnOk() {
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
	@TestedMethod("getById")
	void getById_whenInputIsValid_thenReturnOk() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<AnnouncementDto> response = executeHttpGet("/" + DemoData.ANNOUNCEMENT_ID, requestEntity, AnnouncementDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		AnnouncementDto responseBody = response.getBody();
		assertEquals(DemoData.ANNOUNCEMENT_ID, responseBody.getId());
		assertEquals("title", responseBody.getTitle());
	}
	
	@Test
	@TestedMethod("list")
	void list_whenInputIsValid_thenReturnOk() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<AnnouncementListDto> response = executeHttpGet("/list?page=0&size=10&direction=ASC&property=id", requestEntity, AnnouncementListDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		AnnouncementListDto responseList = response.getBody();
		assertEquals(1, responseList.getResultList().size());
	}
	
	@Test
	@TestedMethod("delete")
	void delete_whenInputIsValid_thenReturnOk() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpDelete("/" + 2, requestEntity,
				String.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
	}
}
