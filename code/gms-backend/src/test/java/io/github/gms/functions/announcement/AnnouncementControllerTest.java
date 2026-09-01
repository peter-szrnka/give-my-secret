package io.github.gms.functions.announcement;

import io.github.gms.abstraction.AbstractClientControllerTest;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Unit test of {@link AnnouncementController}
 * 
 * @author Peter Szrnka
 */
class AnnouncementControllerTest extends AbstractClientControllerTest<AnnouncementService, AnnouncementController> {

    @BeforeEach
    void setupTest() {
        service = mock(AnnouncementService.class);
        controller = new AnnouncementController(service);
    }

    @Test
    void delete_whenInputProvided_thenReturnOk() {
        // when
        ResponseEntity<String> response = controller.delete(1L);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(service).delete(1L);
    }

    @Test
    void save_whenInputProvided_thenReturnOk() {
        // given
        SaveAnnouncementDto dto = TestUtils.createSaveAnnouncementDto();
        when(service.save(dto)).thenReturn(new SaveEntityResponseDto(2L));

        // when
        SaveEntityResponseDto response = controller.save(dto);

        // then
        assertNotNull(response);
        assertEquals(2L, response.getEntityId());
        verify(service).save(dto);
    }

    @Test
    void getById_whenInputProvided_thenReturnOk() {
        // given
        AnnouncementDto dto = TestUtils.createAnnouncementDto();
        when(service.getById(1L)).thenReturn(dto);

        // when
        AnnouncementDto response = controller.getById(1L);

        // then
        assertNotNull(response);
        assertEquals(dto, response);
        verify(service).getById(1L);
    }

    @Test
    void list_whenInputProvided_thenReturnOk() {
        // given
        AnnouncementListDto dtoList = TestUtils.createAnnouncementListDto();
        Pageable pageable = ConverterUtils.createPageable("DESC", "id", 0, 10);
        when(service.list(pageable)).thenReturn(dtoList);

        // when
        AnnouncementListDto response = controller.list(
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
