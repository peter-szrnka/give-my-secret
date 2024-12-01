package io.github.gms.functions.systemproperty;

import io.github.gms.common.util.ConverterUtils;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        service = Mockito.mock(SystemPropertyService.class);
        controller = new SystemPropertyController(service);
    }

    @Test
    void delete_whenInputProvided_thenReturnOk() {
        // arrange
        doNothing().when(service).delete("testKey");

        // act
        ResponseEntity<String> response = controller.delete("testKey");

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(service).delete("testKey");
    }

    @Test
    void save_whenInputProvided_thenReturnOk() {
        // arrange
        SystemPropertyDto dto = TestUtils.createSystemPropertyDto();
        doNothing().when(service).save(dto);

        // act
        ResponseEntity<Void> response = controller.save(dto);

        // assert
         assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        verify(service).save(dto);
    }

    @Test
    void list_whenInputProvided_thenReturnOk() {
        // arrange
        SystemPropertyListDto dtoList = TestUtils.createSystemPropertyListDto();
        Pageable pageable = ConverterUtils.createPageable("DESC", "id", 0, 10);
        when(service.list(pageable)).thenReturn(dtoList);

        // act
        SystemPropertyListDto response = controller.list(
                "DESC",
                "id",
                0,
                10
        );

        // assert
        assertNotNull(response);
        assertEquals(dtoList, response);
        verify(service).list(pageable);
    }
}
