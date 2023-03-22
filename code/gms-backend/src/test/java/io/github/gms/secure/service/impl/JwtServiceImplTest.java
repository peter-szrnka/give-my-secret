package io.github.gms.secure.service.impl;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.secure.service.JwtService;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test of {@link JwtServiceImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class JwtServiceImplTest extends AbstractUnitTest {

	private JwtService service;
	
	@BeforeEach
	void setup() {
		service = new JwtServiceImpl("YXNkZjEyMzQ1Njc4OTBhc2RmMTIzNDU2Nzg5MGFzZGYxMjM0NTY3ODkwYXNkZjEyMzQ1Njc4OTA=");
	}
	
	@Test
	void shouldGenerateJwt() {
		// act
		String response = service.generateJwt(TestUtils.createJwtAdminRequest());

		// assert
		assertNotNull(response);
	}
	
	@Test
	void shouldParseJwt() {
		// arrange
		String generatedToken = service.generateJwt(TestUtils.createJwtAdminRequest());

		// act
		Claims response = service.parseJwt(generatedToken, "HS512");
	
		// assert
		assertNotNull(response);
		assertEquals(DemoData.USER_1_ID, response.get(MdcParameter.USER_ID.getDisplayName(), Long.class));
		assertEquals(DemoData.USERNAME1, response.get(MdcParameter.USER_NAME.getDisplayName()));
		assertTrue(response.get("roles", List.class).contains("ROLE_ADMIN"));
	}
}
