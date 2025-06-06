package io.github.gms.common.service;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class JwtClaimServiceTest extends AbstractUnitTest {
    
    private JwtService jwtService;
    private SystemPropertyService systemPropertyService;
    private JwtClaimService service;

	@BeforeEach
	void setup() {
		jwtService = mock(JwtService.class);
		systemPropertyService = mock(SystemPropertyService.class);
		service = new JwtClaimService(jwtService, systemPropertyService);
	}
	
	@Test
	void getClaims_whenValidJwtProvided_thenReturnClaims() {
        // arrange
        Claims mockClaims = mock(Claims.class);

        when(systemPropertyService.get(SystemProperty.ACCESS_JWT_ALGORITHM)).thenReturn("RSA256");
        when(jwtService.parseJwt("ACCESS_JWT", "RSA256")).thenReturn(mockClaims);

        // act
        Claims response = service.getClaims("ACCESS_JWT");

        // assert
        assertNotNull(response);
        assertEquals(mockClaims, response);
    }
}
