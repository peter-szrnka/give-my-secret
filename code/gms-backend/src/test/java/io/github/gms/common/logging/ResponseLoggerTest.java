package io.github.gms.common.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.dto.SystemStatusDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class ResponseLoggerTest extends AbstractLoggingUnitTest {

    private ObjectMapper objectMapper;
    private ResponseLogger responseLogger;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();

        objectMapper = mock(ObjectMapper.class);
        responseLogger = new ResponseLogger(objectMapper, true);

        addAppender(ResponseLogger.class);
    }

    @Test
    void testSupports() {
        // arrange
        MethodParameter methodParameter = mock(MethodParameter.class);
        Class<StringHttpMessageConverter> converterType = StringHttpMessageConverter.class;

        // act
        assertTrue(responseLogger.supports(methodParameter, converterType));
    }

    @Test
    void testBeforeBodyWrite() throws JsonProcessingException {
        // arrange
        MethodParameter methodParameter = mock(MethodParameter.class);
        MediaType targetType = MediaType.APPLICATION_JSON;
        Class<StringHttpMessageConverter> converterType = StringHttpMessageConverter.class;
        SystemStatusDto body = SystemStatusDto.builder().build();
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        when(objectMapper.writeValueAsString(body)).thenReturn("body");

        // act
        assertEquals(body, responseLogger.beforeBodyWrite(body, methodParameter, targetType, converterType, request, response));
    }

    @Test
    void testBeforeBodyWrite_whenLoggingTurnedOff() throws JsonProcessingException {
        // arrange
        MethodParameter methodParameter = mock(MethodParameter.class);
        MediaType targetType = MediaType.APPLICATION_JSON;
        Class<StringHttpMessageConverter> converterType = StringHttpMessageConverter.class;
        SystemStatusDto body = SystemStatusDto.builder().build();
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        responseLogger = new ResponseLogger(objectMapper, false);

        // act
        assertEquals(body, responseLogger.beforeBodyWrite(body, methodParameter, targetType, converterType, request, response));

        // assert
        verify(objectMapper, never()).writeValueAsString(body);
        assertTrue(logAppender.list.isEmpty());
    }
}