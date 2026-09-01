package io.github.gms.functions.message;

import io.github.gms.common.dto.IdListDto;
import io.github.gms.common.dto.LongValueDto;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Unit test of {@link MessageController}
 * 
 * @author Peter Szrnka
 */
class MessageControllerTest {

    private MessageService service;
    private MessageController controller;
    
    @BeforeEach
    void setupTest() {
        service = mock(MessageService.class);
        controller = new MessageController(service);
    }

    @Test
    void list_whenInputProvided_thenReturnOk() {
        // given
        MessageListDto dtoList = TestUtils.createMessageListDto();
        Pageable pageable = ConverterUtils.createPageable("DESC", "id", 0, 10);
        when(service.list(pageable)).thenReturn(dtoList);

        // when
        MessageListDto response = controller.list(
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

    @Test
    void unreadMessagesCount_whenInputProvided_thenReturnOk() {
        // given
        when(service.getUnreadMessagesCount()).thenReturn(3L);

        // when
        LongValueDto response = controller.unreadMessagesCount();

        // then
        assertNotNull(response);
        assertEquals(3L, response.getValue());
        verify(service).getUnreadMessagesCount();
    }

    @Test
    void markAsRead_whenInputProvided_thenReturnOk() {
        // given
        MarkAsReadRequestDto dto = MarkAsReadRequestDto.builder().build();
        doNothing().when(service).toggleMarkAsRead(dto);

        // when
        ResponseEntity<String> response = controller.markAsRead(dto);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("", response.getBody());
        verify(service).toggleMarkAsRead(dto);
    }

    @Test
    void deleteAllByIds_whenInputProvided_thenReturnOk() {
        // given
        IdListDto dto = new IdListDto(Set.of(1L, 2L, 3L));
        doNothing().when(service).deleteAllByIds(dto);

        // when
        ResponseEntity<Void> response = controller.deleteAllByIds(dto);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(service).deleteAllByIds(dto);
    }
    
    @Test
    void deleteById_whenInputProvided_thenReturnOk() {
        // given
        Long id = 1L;
        doNothing().when(service).deleteById(id);

        // when
        ResponseEntity<Void> response = controller.deleteById(id);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(service).deleteById(id);
    }
}

