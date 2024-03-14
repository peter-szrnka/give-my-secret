package io.github.gms.common.filter;

import io.github.gms.functions.iprestriction.IpRestrictionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A custom Spring filter used to filter incoming API requests by IP address.
 *
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class IpRestrictionFilter extends OncePerRequestFilter {

    private final IpRestrictionService ipRestrictionService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
       try {
           // Ip Restriction
           ipRestrictionService.checkGlobalIpRestrictions();
       } catch (Exception e) {
           response.sendError(HttpStatus.FORBIDDEN.value());
           return;
       }

        filterChain.doFilter(request, response);
    }
}
