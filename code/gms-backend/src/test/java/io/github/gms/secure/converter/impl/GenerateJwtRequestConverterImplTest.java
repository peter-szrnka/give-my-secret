package io.github.gms.secure.converter.impl;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.model.GenerateJwtRequest;
import io.github.gms.secure.service.SystemPropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class GenerateJwtRequestConverterImplTest extends AbstractUnitTest {

    private SystemPropertyService systemPropertyService;
    private GenerateJwtRequestConverterImpl converter;

    @BeforeEach
    void beforeEach() {
        // init
        systemPropertyService = mock(SystemPropertyService.class);
        converter = new GenerateJwtRequestConverterImpl(systemPropertyService);
    }

    @Test
    void shouldConvertToRequest() {
        // arrange
        Map<String, Object> claims = Map.of("A", 1L);
        when(systemPropertyService.get(any(SystemProperty.class))).thenReturn("RSA");
        when(systemPropertyService.getLong(any(SystemProperty.class))).thenReturn(30L);

        // act
        GenerateJwtRequest response = converter.toRequest(JwtConfigType.ACCESS_JWT, "subject", claims);

        // assert
        assertNotNull(response);
        assertEquals("subject", response.getSubject());
        assertEquals(1, response.getClaims().size());
        assertEquals(30L, response.getExpirationDateInSeconds());
        verify(systemPropertyService).get(any(SystemProperty.class));
        verify(systemPropertyService).getLong(any(SystemProperty.class));
    }
}
