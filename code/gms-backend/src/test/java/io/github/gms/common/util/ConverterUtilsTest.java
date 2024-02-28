package io.github.gms.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.dto.PagingDto;

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
        PagingDto dto = new PagingDto("DESC", "id", 1, 10);
        // act
        Pageable response = ConverterUtils.createPageable(dto);

        // assert
        assertNotNull(response);
        assertTrue(response.getSort().isSorted());
        assertEquals(1, response.getPageNumber());
        assertEquals(10, response.getOffset());
        assertEquals(10, response.getPageSize());
    }
}
