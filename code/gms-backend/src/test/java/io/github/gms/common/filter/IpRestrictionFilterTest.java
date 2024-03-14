package io.github.gms.common.filter;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.functions.iprestriction.IpRestrictionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class IpRestrictionFilterTest extends AbstractUnitTest {

    private IpRestrictionService ipRestrictionService;
    private IpRestrictionFilter filter;

    @BeforeEach
    void setup() {
        // init
        ipRestrictionService = mock(IpRestrictionService.class);
        filter = new IpRestrictionFilter(ipRestrictionService);
    }

    @Test
    @SneakyThrows
    void shouldRun() {
        // act
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, filterChain);

        // assert
        verify(ipRestrictionService).checkGlobalIpRestrictions();
        verify(response, never()).sendError(HttpStatus.FORBIDDEN.value());
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    @SneakyThrows
    void shouldHandleError() {
        // arrange
        doThrow(new IllegalArgumentException()).when(ipRestrictionService).checkGlobalIpRestrictions();

        // act
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);


        filter.doFilterInternal(request, response, filterChain);

        // assert
        verify(ipRestrictionService).checkGlobalIpRestrictions();
        verify(response).sendError(HttpStatus.FORBIDDEN.value());
        verify(filterChain, never()).doFilter(any(), any());
    }
}
