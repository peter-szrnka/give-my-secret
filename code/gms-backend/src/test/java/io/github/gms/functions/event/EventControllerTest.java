package io.github.gms.functions.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.gms.abstraction.AbstractClientControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.github.gms.common.dto.PagingDto;
import io.github.gms.util.TestUtils;

/**
 * Unit test of {@link EventController}
 * 
 * @author Peter Szrnka
 */
class EventControllerTest extends AbstractClientControllerTest<EventService, EventController> {
    
    @BeforeEach
    void setupTest() {
        service = Mockito.mock(EventService.class);
        controller = new EventController(service);
    }

    @Test
    void shouldReturnList() {
        // arrange
        PagingDto pagingDto = new PagingDto("DESC", "id", 0, 10);
        EventListDto dtoList = TestUtils.createEventListDto();
        when(service.list(pagingDto)).thenReturn(dtoList);
        

        // act
        EventListDto response = controller.list(pagingDto);

        // assert
        assertNotNull(response);
        assertEquals(dtoList, response);
        verify(service).list(pagingDto);
    }

    @Test
    void shouldReturnListByUserId() {
        // arrange
        PagingDto pagingDto = new PagingDto("DESC", "id", 0, 10);
        EventListDto dtoList = TestUtils.createEventListDto();
        when(service.listByUser(1L, pagingDto)).thenReturn(dtoList);
        

        // act
        EventListDto response = controller.listByUserId(1L, pagingDto);

        // assert
        assertNotNull(response);
        assertEquals(dtoList, response);
        verify(service).listByUser(1L, pagingDto);
    }
}
