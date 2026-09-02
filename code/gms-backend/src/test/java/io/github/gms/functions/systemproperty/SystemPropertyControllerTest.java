package io.github.gms.functions.systemproperty;

import io.github.gms.common.util.ConverterUtils;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test of {@link SystemPropertyController}
 * 
 * @author Peter Szrnka
 * @version 1.0
 */
class SystemPropertyControllerTest {

    private SystemPropertyController controller;
    private SystemPropertyService service;
    
    @BeforeEach
    void setup() {
        service = mock(SystemPropertyService.class);
        controller = new SystemPropertyController(service);
    }

    @Test
    void delete_whenInputProvided_thenReturnOk() {
        // given
        doNothing().when(service).delete("testKey");

        // when
        ResponseEntity<String> response = controller.delete("testKey");

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(service).delete("testKey");
    }

    @Test
    void save_whenInputProvided_thenReturnOk() {
        // given
        SystemPropertyDto dto = TestUtils.createSystemPropertyDto();
        doNothing().when(service).save(dto);

        // when
        ResponseEntity<Void> response = controller.save(dto);

        // then
         assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        verify(service).save(dto);
    }

    @Test
    void list_whenInputProvided_thenReturnOk() {
        // given
        SystemPropertyListDto dtoList = TestUtils.createSystemPropertyListDto();
        Pageable pageable = ConverterUtils.createPageable("DESC", "id", 0, 10);
        when(service.list(pageable)).thenReturn(dtoList);

        // when
        SystemPropertyListDto response = controller.list(
                "DESC",
                "id",
                0,
                10
        );

        // then
        assertNotNull(response);
        assertEquals(dtoList, response);
        verify(service).list(pageable);
    }
}

