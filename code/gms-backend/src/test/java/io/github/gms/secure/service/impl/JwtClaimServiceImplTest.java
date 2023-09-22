package io.github.gms.secure.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.secure.service.JwtService;
import io.github.gms.secure.service.SystemPropertyService;
import io.jsonwebtoken.Claims;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class JwtClaimServiceImplTest extends AbstractUnitTest {
    
    private JwtService jwtService;
    private SystemPropertyService systemPropertyService;
    private JwtClaimServiceImpl service;

	@BeforeEach
	public void setup() {
		jwtService = mock(JwtService.class);
		systemPropertyService = mock(SystemPropertyService.class);
		service = new JwtClaimServiceImpl(jwtService, systemPropertyService);
	}
	
	@Test
	void shouldReturnClaims() {
        // arrange
        Claims mockClaims = mock(Claims.class);

        when(systemPropertyService.get(SystemProperty.ACCESS_JWT_ALGORITHM)).thenReturn("RSA256");
        when(jwtService.parseJwt(eq("ACCESS_JWT"), eq("RSA256"))).thenReturn(mockClaims);

        // act
        Claims response = service.getClaims("ACCESS_JWT");

        // assert
        assertNotNull(response);
        assertEquals(mockClaims, response);
    }
}
