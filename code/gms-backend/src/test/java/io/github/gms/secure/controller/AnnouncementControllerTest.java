package io.github.gms.secure.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import io.github.gms.secure.dto.AnnouncementDto;
import io.github.gms.secure.dto.AnnouncementListDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveAnnouncementDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.service.AnnouncementService;
import io.github.gms.util.TestUtils;

/**
 * Unit test of {@link AnnouncementController}
 * 
 * @author Peter Szrnka
 */
class AnnouncementControllerTest extends AbstractClientControllerTest<AnnouncementService, AnnouncementController> {

    @BeforeEach
    void setupTest() {
        service = Mockito.mock(AnnouncementService.class);
        controller = new AnnouncementController(service);
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

    @Test
    void shouldSave() {
        // arrange
        SaveAnnouncementDto dto = TestUtils.createSaveAnnouncementDto();
        when(service.save(dto)).thenReturn(new SaveEntityResponseDto(2L));

        // act
        SaveEntityResponseDto response = controller.save(dto);

        // assert
        assertNotNull(response);
        assertEquals(2L, response.getEntityId());
        verify(service).save(dto);
    }

    @Test
    void shouldReturnById() {
        // arrange
        AnnouncementDto dto = TestUtils.createAnnouncementDto();
        when(service.getById(1L)).thenReturn(dto);

        // act
        AnnouncementDto response = controller.getById(1L);

        // assert
        assertNotNull(response);
        assertEquals(dto, response);
        verify(service).getById(1L);
    }

    @Test
    void shouldReturnList() {
        // arrange
        PagingDto pagingDto = new PagingDto("DESC", "id", 0, 10);
        AnnouncementListDto dtoList = TestUtils.createAnnouncementListDto();
        when(service.list(pagingDto)).thenReturn(dtoList);
        

        // act
        AnnouncementListDto response = controller.list(pagingDto);

        // assert
        assertNotNull(response);
        assertEquals(dtoList, response);
        verify(service).list(pagingDto);
    }
}