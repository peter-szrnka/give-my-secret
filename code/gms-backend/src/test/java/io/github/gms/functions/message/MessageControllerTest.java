package io.github.gms.functions.message;

import io.github.gms.common.dto.IdListDto;
import io.github.gms.common.dto.LongValueDto;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        service = Mockito.mock(MessageService.class);
        controller = new MessageController(service);
    }

    @Test
    void shouldReturnList() {
        // arrange
        MessageListDto dtoList = TestUtils.createMessageListDto();
        Pageable pageable = ConverterUtils.createPageable("DESC", "id", 0, 10);
        when(service.list(pageable)).thenReturn(dtoList);

        // act
        MessageListDto response = controller.list(
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
    void shouldReturnUnreadMessagesCount() {
        // arrange
        when(service.getUnreadMessagesCount()).thenReturn(3L);

        // act
        LongValueDto response = controller.unreadMessagesCount();

        // assert
        assertNotNull(response);
        assertEquals(3L, response.getValue());
        verify(service).getUnreadMessagesCount();
    }

    @Test
    void shouldMarkAsRead() {
        // arrange
        MarkAsReadRequestDto dto = MarkAsReadRequestDto.builder().build();
        doNothing().when(service).markAsRead(dto);

        // act
        ResponseEntity<String> response = controller.markAsRead(dto);

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("", response.getBody());
        verify(service).markAsRead(dto);
    }

    @Test
    void shouldDeleteAllByIds() {
        // arrange
        IdListDto dto = new IdListDto(Set.of(1L, 2L, 3L));
        doNothing().when(service).deleteAllByIds(dto);

        // act
        ResponseEntity<Void> response = controller.deleteAllByIds(dto);

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(service).deleteAllByIds(dto);
    }

    @Test
    void shouldToggleReadByIds() {
        // arrange
        IdListDto dto = new IdListDto(Set.of(1L, 2L, 3L));
        doNothing().when(service).toggleReadByIds(dto, true);

        // act
        ResponseEntity<Void> response = controller.toggleReadByIds(dto, true);

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(service).toggleReadByIds(dto, true);
    }
    
    @Test
    void shouldDeleteById() {
        // arrange
        Long id = 1L;
        doNothing().when(service).deleteById(id);

        // act
        ResponseEntity<Void> response = controller.deleteById(id);

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(service).deleteById(id);
    }
}
