package io.github.gms.secure.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.model.GenerateJwtRequest;
import io.github.gms.secure.service.JwtService;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;

/**
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
	void shouldgenerateJwts() {
		// arrange
		GenerateJwtRequest generateRequest1 = new GenerateJwtRequest("subject1", "HS384", 1000L, Map.of("k1", "v1"));
		GenerateJwtRequest generateRequest2 = new GenerateJwtRequest("subject2", "HS384", 1000L, Map.of("k2", "v2"));
		Map<JwtConfigType, GenerateJwtRequest> request = Map.of(
			JwtConfigType.ACCESS_JWT, generateRequest1, 
			JwtConfigType.REFRESH_JWT, generateRequest2
		);
		
		// act
		Map<JwtConfigType, String> response = service.generateJwts(request);

		// assert
		assertNotNull(response);
		assertEquals(2, response.size());
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
