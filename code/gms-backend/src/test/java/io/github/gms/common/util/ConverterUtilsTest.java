package io.github.gms.common.util;

import io.github.gms.abstraction.AbstractUnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class ConverterUtilsTest extends AbstractUnitTest {

    @Test
    void shouldTestPrivateConstructor() {
        assertPrivateConstructor(ConverterUtils.class);
    }
    
    @Test
    void shouldCreatePageable() {
        // act
        Pageable response = ConverterUtils.createPageable("DESC", "id", 1, 10);

        // assert
        assertNotNull(response);
        assertTrue(response.getSort().isSorted());
        assertEquals(1, response.getPageNumber());
        assertEquals(10, response.getOffset());
        assertEquals(10, response.getPageSize());
    }
}
