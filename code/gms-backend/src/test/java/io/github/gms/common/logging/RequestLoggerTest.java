package io.github.gms.common.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.dto.SystemStatusDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.io.IOException;
import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class RequestLoggerTest extends AbstractLoggingUnitTest {

    private RequestLogger requestLogger;
    private final ObjectMapper objectMapper = mock(ObjectMapper.class);

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        requestLogger = new RequestLogger(objectMapper, true);
        addAppender(RequestLogger.class);
    }

    @Test
    void testSupports() {
        MethodParameter methodParameter = mock(MethodParameter.class);
        Type targetType = mock(Type.class);
        Class<StringHttpMessageConverter> converterType = StringHttpMessageConverter.class;
        assertTrue(requestLogger.supports(methodParameter, targetType, converterType));
    }

    @Test
    void testBeforeBodyRead() throws IOException {
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        MethodParameter methodParameter = mock(MethodParameter.class);
        Type targetType = mock(Type.class);
        Class<StringHttpMessageConverter> converterType = StringHttpMessageConverter.class;
        assertEquals(inputMessage, requestLogger.beforeBodyRead(inputMessage, methodParameter, targetType, converterType));
    }

    @Test
    void testAfterBodyRead() throws JsonProcessingException {
        // arrange
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        MethodParameter methodParameter = mock(MethodParameter.class);
        Type targetType = mock(Type.class);
        Class<StringHttpMessageConverter> converterType = StringHttpMessageConverter.class;
        SystemStatusDto body = SystemStatusDto.builder().build();
        when(objectMapper.writeValueAsString(body)).thenReturn("body");

        // act
        assertEquals(body, requestLogger.afterBodyRead(body, inputMessage, methodParameter, targetType, converterType));

        // assert
        verify(objectMapper).writeValueAsString(body);
        assertTrue(logAppender.list.stream().anyMatch(event -> event.getFormattedMessage().equals("Request logged: body")));
    }

    @Test
    void testAfterBodyRead_whenLoggingTurnedOff() throws JsonProcessingException {
        // arrange
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        MethodParameter methodParameter = mock(MethodParameter.class);
        Type targetType = mock(Type.class);
        Class<StringHttpMessageConverter> converterType = StringHttpMessageConverter.class;
        SystemStatusDto body = SystemStatusDto.builder().build();
        requestLogger = new RequestLogger(objectMapper, false);

        // act
        assertEquals(body, requestLogger.afterBodyRead(body, inputMessage, methodParameter, targetType, converterType));

        // assert
        verify(objectMapper, never()).writeValueAsString(body);
        assertTrue(logAppender.list.isEmpty());
    }

    @Test
    void testAfterBodyRead_whenExceptionOccurs() throws JsonProcessingException {
        // arrange
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        MethodParameter methodParameter = mock(MethodParameter.class);
        Type targetType = mock(Type.class);
        Class<StringHttpMessageConverter> converterType = StringHttpMessageConverter.class;
        SystemStatusDto body = SystemStatusDto.builder().build();
        when(objectMapper.writeValueAsString(body)).thenThrow(JsonProcessingException.class);

        // act
        assertEquals(body, requestLogger.afterBodyRead(body, inputMessage, methodParameter, targetType, converterType));

        // assert
        assertTrue(logAppender.list.stream().anyMatch(event -> event.getFormattedMessage().startsWith("Error while logging request")));
    }

    @Test
    void testHandleEmptyBody() {
        // arrange
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        MethodParameter methodParameter = mock(MethodParameter.class);
        Type targetType = mock(Type.class);
        Class<StringHttpMessageConverter> converterType = StringHttpMessageConverter.class;
        SystemStatusDto body = SystemStatusDto.builder().build();

        // act
        assertEquals(body, requestLogger.handleEmptyBody(body, inputMessage, methodParameter, targetType, converterType));

        // assert
        assertTrue(logAppender.list.isEmpty());
    }
}
