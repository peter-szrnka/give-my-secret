package io.github.gms.functions.systemproperty;

import io.github.gms.common.dto.PagingDto;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
    void setupTest() {
        service = Mockito.mock(SystemPropertyService.class);
        controller = new SystemPropertyController(service);
    }

    @Test
    void shouldDeleteEntity() {
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
    void shouldSave() {
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
    void shouldReturnList() {
        // arrange
        PagingDto pagingDto = new PagingDto("DESC", "id", 0, 10);
        SystemPropertyListDto dtoList = TestUtils.createSystemPropertyListDto();
        when(service.list(pagingDto)).thenReturn(dtoList);
        

        // act
        SystemPropertyListDto response = controller.list(pagingDto);

        // assert
        assertNotNull(response);
        assertEquals(dtoList, response);
        verify(service).list(pagingDto);
    }
}
