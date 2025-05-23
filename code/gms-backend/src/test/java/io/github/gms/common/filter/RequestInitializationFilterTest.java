package io.github.gms.common.filter;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.MdcParameter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.slf4j.MDC;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.Clock;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.github.gms.util.LogAssertionUtils.assertLogEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class RequestInitializationFilterTest extends AbstractLoggingUnitTest {

    private Clock clock;
    private RequestInitializationFilter filter ;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        clock = mock(Clock.class);
        filter = new RequestInitializationFilter(clock);
        addAppender(RequestInitializationFilter.class);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void doFilterInternal_whenContextIsOk_thenInitializeRequest(boolean logResponseTimeDisabled) throws ServletException, IOException {
        ReflectionTestUtils.setField(filter, "logResponseTimeDisabled", logResponseTimeDisabled);

        try (MockedStatic<MDC> mdcMockedStatic = mockStatic(MDC.class)) {
            // arrange
            AtomicBoolean atomicInteger = new AtomicBoolean(false);
            when(clock.millis()).thenAnswer(invocation -> {
                if (atomicInteger.get()) {
                    return 100L;
                } else {
                    atomicInteger.set(true);
                    return 0L;
                }
            });
            HttpServletRequest request = mock(HttpServletRequest.class);
            if (!logResponseTimeDisabled) {
                when(request.getRequestURI()).thenReturn("/test");
            }
            HttpServletResponse response = mock(HttpServletResponse.class);
            FilterChain filterChain = mock(FilterChain.class);
            mdcMockedStatic.when(() -> MDC.get(MdcParameter.CORRELATION_ID.getDisplayName())).thenReturn("MOCK_CORRELATION_ID");

            // act
            filter.doFilterInternal(request, response, filterChain);

            // assert
            verify(response).addHeader("X-CORRELATION-ID", "MOCK_CORRELATION_ID");
            verify(filterChain).doFilter(request, response);
            mdcMockedStatic.verify(() -> MDC.get(MdcParameter.CORRELATION_ID.getDisplayName()));
            mdcMockedStatic.verify(() -> MDC.put(eq(MdcParameter.CORRELATION_ID.getDisplayName()), anyString()));
            mdcMockedStatic.verify(() -> MDC.remove(MdcParameter.CORRELATION_ID.getDisplayName()));
            verify(clock, times(logResponseTimeDisabled ? 1 : 2)).millis();

            if (!logResponseTimeDisabled) {
                assertLogEquals(logAppender, "Request: uri=/test took duration=100 ms");
            }
        }
    }
}
