package io.github.gms.functions.iprestriction;

import io.github.gms.common.dto.PagingDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test of {@link IpRestrictionController}
 *
 * @author Peter Szrnka
 * @since 1.0
 */
class IpRestrictionControllerTest {

    private IpRestrictionController controller;
    private IpRestrictionService service;

    @BeforeEach
    void setupTest() {
        service = Mockito.mock(IpRestrictionService.class);
        controller = new IpRestrictionController(service);
    }

    @Test
    void shouldSave() {
        // arrange
        IpRestrictionDto dto = TestUtils.createIpRestrictionDto();
        when(service.save(dto)).thenReturn(new SaveEntityResponseDto(1L));

        // act
        SaveEntityResponseDto response = controller.save(dto);

        // assert
        assertNotNull(response);
        assertEquals(1L, response.getEntityId());
        verify(service).save(dto);
    }

    @Test
    void shouldReturnById() {
        // arrange
        IpRestrictionDto dto = TestUtils.createIpRestrictionDto();
        when(service.getById(1L)).thenReturn(dto);

        // act
        IpRestrictionDto response = controller.getById(1L);

        // assert
        assertNotNull(response);
        assertEquals(dto, response);
        verify(service).getById(1L);
    }

    @Test
    void shouldReturnList() {
        // arrange
        PagingDto pagingDto = new PagingDto("DESC", "id", 0, 10);
        IpRestrictionListDto dtoList = TestUtils.createIpRestrictionListDto();
        when(service.list(pagingDto)).thenReturn(dtoList);


        // act
        IpRestrictionListDto response = controller.list(pagingDto);

        // assert
        assertNotNull(response);
        assertEquals(dtoList, response);
        verify(service).list(pagingDto);
    }

    @Test
    void shouldDeleteEntity() {
        // arrange
        doNothing().when(service).delete(1L);

        // act
        ResponseEntity<String> response = controller.delete(1L);

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(service).delete(1L);
    }
}
