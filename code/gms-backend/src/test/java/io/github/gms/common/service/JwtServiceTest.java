package io.github.gms.common.service;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.model.GenerateJwtRequest;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class JwtServiceTest extends AbstractUnitTest {

	private static final String SECRET = "YXNkZjEyMzQ1Njc4OTBhc2RmMTIzNDU2Nzg5MGFzZGYxMjM0NTY3ODkwYXNkZjEyMzQ1Njc4OTA=";
	private Clock clock;
	private JwtService service;
	
	@BeforeEach
	void setup() {
		clock = mock(Clock.class);
		service = new JwtService(clock, SECRET);
	}

	@Test
	@SneakyThrows
	void generateJwts_whenInputIsValid_thenReturnJwts() {
		// arrange
		when(clock.instant()).thenReturn(Instant.now().plusSeconds(900L));
		GenerateJwtRequest generateRequest1 = new GenerateJwtRequest("subject1", "HS384", 1000L, Map.of("k1", "v1"));
		GenerateJwtRequest generateRequest2 = new GenerateJwtRequest("subject2", "HS384", 1000L, Map.of("k2", "v2"));
		Map<JwtConfigType, GenerateJwtRequest> request = Map.of(
			JwtConfigType.ACCESS_JWT, generateRequest1, 
			JwtConfigType.REFRESH_JWT, generateRequest2
		);
		MockedStatic<UUID> mockedUUID = mockStatic(UUID.class);
		UUID mockUUID = mock(UUID.class);
		when(mockUUID.toString()).thenReturn("123456-1234-1234-123456");
		mockedUUID.when(UUID::randomUUID).thenReturn(mockUUID);
		
		// act
		Map<JwtConfigType, String> response = service.generateJwts(request);

		// assert
		assertNotNull(response);
		assertEquals(2, response.size());

		// verify access jwt
		Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(SECRET), SignatureAlgorithm.forName("HS384").getJcaName());
		Claims claims = Jwts.parserBuilder().setSigningKey(hmacKey).build().parseClaimsJws(response.get(JwtConfigType.ACCESS_JWT)).getBody();
		
		assertEquals("subject1", claims.getSubject());
		assertNull(claims.getAudience());
		assertEquals("v1", claims.get("k1"));
		assertEquals("123456-1234-1234-123456", claims.getId());
		assertNotNull(claims.getIssuedAt().toString());
		assertNotNull(claims.getExpiration());

		mockedUUID.close();
	}
	
	@Test
	void generateJwt_whenInputIsAdmin_thenReturnJwt() {
		// arrange
		when(clock.instant()).thenReturn(Instant.now().plusSeconds(900L));
		// act
		String response = service.generateJwt(TestUtils.createJwtAdminRequest());

		// assert
		assertNotNull(response);
	}
	
	@Test
	void parseJwt_whenJwtIsValid_thenReturnClaims() {
		// arrange
		when(clock.instant()).thenReturn(Instant.now().plusSeconds(900L));
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
