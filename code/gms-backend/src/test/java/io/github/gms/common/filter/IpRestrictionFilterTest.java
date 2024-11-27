package io.github.gms.common.filter;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.model.IpRestrictionPatterns;
import io.github.gms.functions.iprestriction.IpRestrictionService;
import io.github.gms.functions.iprestriction.IpRestrictionValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class IpRestrictionFilterTest extends AbstractUnitTest {

    private IpRestrictionService ipRestrictionService;
    private IpRestrictionValidator validator;
    private IpRestrictionFilter filter;

    @BeforeEach
    void setup() {
        // init
        ipRestrictionService = mock(IpRestrictionService.class);
        validator = mock(IpRestrictionValidator.class);
        filter = new IpRestrictionFilter(ipRestrictionService, validator);
    }

    @Test
    @SneakyThrows
    void doFilterInternal_whenAddressIsNotBlocked_thenReturnOk() {
        // arrange
        when(ipRestrictionService.checkGlobalIpRestrictions()).thenReturn(new IpRestrictionPatterns(List.of()));

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
    void doFilterInternal_whenAddressIsBlocked_thenReturnForbidden() {
        // arrange
        when(ipRestrictionService.checkGlobalIpRestrictions()).thenReturn(new IpRestrictionPatterns(List.of()));
        when(validator.isIpAddressBlocked(anyList())).thenReturn(true);

        // act
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);


        filter.doFilterInternal(request, response, filterChain);

        // assert
        verify(validator).isIpAddressBlocked(anyList());
        verify(response).sendError(HttpStatus.FORBIDDEN.value(), "You are not allowed to get this secret from your IP address!");
        verify(filterChain, never()).doFilter(any(), any());
    }
}
