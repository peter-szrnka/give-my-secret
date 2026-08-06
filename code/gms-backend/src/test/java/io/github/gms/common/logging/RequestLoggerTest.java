package io.github.gms.common.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.dto.SystemStatusDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.StringHttpMessageConverter;
import tools.jackson.databind.json.JsonMapper;

import java.lang.reflect.Type;

import static io.github.gms.util.LogAssertionUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class RequestLoggerTest extends AbstractLoggingUnitTest {

    private final JsonMapper jsonMapper = mock(JsonMapper.class);
    private final JsonMapper sensitiveLoggingJsonMapper = mock(JsonMapper.class);
    private RequestLogger requestLogger;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        requestLogger = new RequestLogger(jsonMapper, sensitiveLoggingJsonMapper, true, true);
        addAppender(BasePayloadLogger.class);
    }

    @Test
    void supports_whenCalled_thenReturnTrue() {
        MethodParameter methodParameter = mock(MethodParameter.class);
        Type targetType = mock(Type.class);
        Class<StringHttpMessageConverter> converterType = StringHttpMessageConverter.class;
        assertTrue(requestLogger.supports(methodParameter, targetType, converterType));
    }

    @Test
    void beforeBodyRead_whenCalled_thenReturnHttpInputMessage() {
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        MethodParameter methodParameter = mock(MethodParameter.class);
        Type targetType = mock(Type.class);
        Class<StringHttpMessageConverter> converterType = StringHttpMessageConverter.class;
        assertEquals(inputMessage, requestLogger.beforeBodyRead(inputMessage, methodParameter, targetType, converterType));
    }

    @Test
    void afterBodyRead_whenMaskingDisabled_thenSkipLogRequestBody() throws JsonProcessingException {
        // arrange
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        MethodParameter methodParameter = mock(MethodParameter.class);
        Type targetType = mock(Type.class);
        Class<StringHttpMessageConverter> converterType = StringHttpMessageConverter.class;
        SystemStatusDto body = SystemStatusDto.builder().build();
        when(jsonMapper.writeValueAsString(body)).thenReturn("body");
        requestLogger = new RequestLogger(jsonMapper, sensitiveLoggingJsonMapper, false, true);

        // act
        assertEquals(body, requestLogger.afterBodyRead(body, inputMessage, methodParameter, targetType, converterType));

        // assert
        verify(sensitiveLoggingJsonMapper, never()).writeValueAsString(body);
        verify(jsonMapper).writeValueAsString(body);
        assertLogEquals(logAppender, "Request logged: body");
    }

    @Test
    void afterBodyRead_whenMaskingEnabled_thenLogRequestBody() throws JsonProcessingException {
        // arrange
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        MethodParameter methodParameter = mock(MethodParameter.class);
        Type targetType = mock(Type.class);
        Class<StringHttpMessageConverter> converterType = StringHttpMessageConverter.class;
        SystemStatusDto body = SystemStatusDto.builder().build();
        when(sensitiveLoggingJsonMapper.writeValueAsString(body)).thenReturn("body");

        // act
        assertEquals(body, requestLogger.afterBodyRead(body, inputMessage, methodParameter, targetType, converterType));

        // assert
        verify(sensitiveLoggingJsonMapper).writeValueAsString(body);
        verify(jsonMapper, never()).writeValueAsString(body);
        assertLogEquals(logAppender, "Request logged: body");
    }

    @Test
    void afterBodyRead_whenLoggingTurnedOff_thenLogSkipped() throws JsonProcessingException {
        // arrange
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        MethodParameter methodParameter = mock(MethodParameter.class);
        Type targetType = mock(Type.class);
        Class<StringHttpMessageConverter> converterType = StringHttpMessageConverter.class;
        SystemStatusDto body = SystemStatusDto.builder().build();
        requestLogger = new RequestLogger(jsonMapper, sensitiveLoggingJsonMapper, true,false);

        // act
        assertEquals(body, requestLogger.afterBodyRead(body, inputMessage, methodParameter, targetType, converterType));

        // assert
        verify(sensitiveLoggingJsonMapper, never()).writeValueAsString(body);
        verify(jsonMapper, never()).writeValueAsString(body);
        assertTrue(logAppender.list.isEmpty());
    }

    @Test
    void afterBodyRead_whenExceptionOccurs_thenLogException() {
        // arrange
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        MethodParameter methodParameter = mock(MethodParameter.class);
        Type targetType = mock(Type.class);
        Class<StringHttpMessageConverter> converterType = StringHttpMessageConverter.class;
        SystemStatusDto body = SystemStatusDto.builder().build();
        when(sensitiveLoggingJsonMapper.writeValueAsString(body)).thenThrow(RuntimeException.class);

        // act
        assertEquals(body, requestLogger.afterBodyRead(body, inputMessage, methodParameter, targetType, converterType));

        // assert
        assertLogStartsWith(logAppender, "Error while logging request");
    }

    @Test
    void handleEmptyBody_whenCalled_thenReturnBody() {
        // arrange
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        MethodParameter methodParameter = mock(MethodParameter.class);
        Type targetType = mock(Type.class);
        Class<StringHttpMessageConverter> converterType = StringHttpMessageConverter.class;
        SystemStatusDto body = SystemStatusDto.builder().build();

        // act
        assertEquals(body, requestLogger.handleEmptyBody(body, inputMessage, methodParameter, targetType, converterType));

        // assert
        assertLogEmpty(logAppender);
    }
}
