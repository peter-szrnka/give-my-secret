package io.github.gms.functions.event;

import io.github.gms.abstraction.AbstractClientControllerTest;
import io.github.gms.common.dto.IntegerValueDto;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test of {@link EventController}
 * 
 * @author Peter Szrnka
 */
class EventControllerTest extends AbstractClientControllerTest<EventService, EventController> {
    
    @BeforeEach
    void setup() {
        service = Mockito.mock(EventService.class);
        controller = new EventController(service);
    }

    @Test
    void list_whenInputProvided_thenReturnOk() {
        // arrange
        EventListDto dtoList = TestUtils.createEventListDto();
        Pageable pageable = ConverterUtils.createPageable("DESC", "id", 0, 10);
        when(service.list(pageable)).thenReturn(dtoList);
        

        // act
        EventListDto response = controller.list(
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

    @Test
    void listByUserId_whenInputProvided_thenReturnOk() {
        // arrange
        Pageable pageable = ConverterUtils.createPageable("DESC", "id", 0, 10);
        EventListDto dtoList = TestUtils.createEventListDto();
        when(service.listByUser(1L, pageable)).thenReturn(dtoList);
        

        // act
        EventListDto response = controller.listByUserId(1L,
                "DESC",
                "id",
                0,
                10);

        // assert
        assertNotNull(response);
        assertEquals(dtoList, response);
        verify(service).listByUser(1L, pageable);
    }

    @Test
    void getUnprocessedEventsCount_thenReturnOk() {
        when(service.getUnprocessedEventsCount()).thenReturn(1);

        ResponseEntity<IntegerValueDto> response = controller.getUnprocessedEventsCount();

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getValue());
        verify(service).getUnprocessedEventsCount();
    }
}
