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

import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.MarkAsReadRequestDto;
import io.github.gms.secure.dto.MessageListDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.service.MessageService;
import io.github.gms.util.TestUtils;

/**
 * Unit test of {@link MessageController}
 * 
 * @author Peter Szrnka
 */
class MessageControllerTest extends AbstractClientControllerTest<MessageService, MessageController> {
    
    @BeforeEach
    void setupTest() {
        service = Mockito.mock(MessageService.class);
        controller = new MessageController(service);
    }

    @Test
    void shouldReturnList() {
        // arrange
        PagingDto pagingDto = new PagingDto("DESC", "id", 0, 10);
        MessageListDto dtoList = TestUtils.createMessageListDto();
        when(service.list(pagingDto)).thenReturn(dtoList);

        // act
        MessageListDto response = controller.list(pagingDto);

        // assert
        assertNotNull(response);
        assertEquals(dtoList, response);
        verify(service).list(pagingDto);
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
}
