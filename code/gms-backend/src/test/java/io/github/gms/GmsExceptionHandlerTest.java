package io.github.gms;

import io.github.gms.common.dto.ErrorResponseDto;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.types.GmsException;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingRequestHeaderException;

import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Disabled
@ExtendWith(MockitoExtension.class)
class GmsExceptionHandlerTest {

    private static final String CORRELATION_ID = "CORRELATION_ID";
    private static final ZonedDateTime zonedDateTime =
            ZonedDateTime.ofInstant(Instant.parse("2023-06-29T00:00:00Z"), ZoneId.systemDefault());

    private GmsExceptionHandler handler;
    private Clock clock;

    @BeforeEach
    void setup() {
        MDC.put(MdcParameter.CORRELATION_ID.getDisplayName(), CORRELATION_ID);
        clock = mock(Clock.class);
        handler = new GmsExceptionHandler(clock);
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldHandleGmsException() {
        try (MockedStatic<ZonedDateTime> mockedZonedDateTime = mockStatic(ZonedDateTime.class)) {
            mockedZonedDateTime.when(() -> ZonedDateTime.now(clock)).thenReturn(zonedDateTime);
            // act
            ResponseEntity<ErrorResponseDto> response = handler.handleGmsException(new GmsException("Oops!"));

            // assert
            assertNotNull(response);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(CORRELATION_ID, response.getBody().getCorrelationId());
            assertEquals("Oops!", response.getBody().getMessage());
            mockedZonedDateTime.verify(() -> ZonedDateTime.now(clock));
        }
    }

    @Test
    void shouldHandleAccessDeniedException() {
        try (MockedStatic<ZonedDateTime> mockedZonedDateTime = mockStatic(ZonedDateTime.class)) {
            mockedZonedDateTime.when(() -> ZonedDateTime.now(clock)).thenReturn(zonedDateTime);
            // act
            ResponseEntity<ErrorResponseDto> response = handler.handleAccessDeniedException(new AccessDeniedException("Oops!"));

            // assert
            assertNotNull(response);
            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(CORRELATION_ID, response.getBody().getCorrelationId());
            assertEquals("Oops!", response.getBody().getMessage());
            mockedZonedDateTime.verify(() -> ZonedDateTime.now(clock));
        }
    }

    @Test
    void shouldHandleOtherException() {
        try (MockedStatic<ZonedDateTime> mockedZonedDateTime = mockStatic(ZonedDateTime.class)) {
            mockedZonedDateTime.when(() -> ZonedDateTime.now(clock)).thenReturn(zonedDateTime);
            // act
            ResponseEntity<ErrorResponseDto> response = handler.handleOtherException(new RuntimeException("Oops!"));

            // assert
            assertNotNull(response);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(CORRELATION_ID, response.getBody().getCorrelationId());
            assertEquals("Oops!", response.getBody().getMessage());
            mockedZonedDateTime.verify(() -> ZonedDateTime.now(clock));
        }
    }

    @Test
    void shouldHandleMissingRequestHeaderException() {
        try (MockedStatic<ZonedDateTime> mockedZonedDateTime = mockStatic(ZonedDateTime.class)) {
            mockedZonedDateTime.when(() -> ZonedDateTime.now(clock)).thenReturn(zonedDateTime);
            // arrange
            Method mockMethod = mock(Method.class);
            MethodParameter mockMethodParameter = new MethodParameter(mockMethod, -1, 2);

            // act
            ResponseEntity<ErrorResponseDto> response = handler.handleMissingRequestHeaderException(new MissingRequestHeaderException("x-api-key", mockMethodParameter));

            // assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(CORRELATION_ID, response.getBody().getCorrelationId());
            assertEquals("Required request header 'x-api-key' for method parameter type Object is not present", response.getBody().getMessage());
            mockedZonedDateTime.verify(() -> ZonedDateTime.now(clock));
        }
    }
}