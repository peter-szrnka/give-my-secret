package io.github.gms.common.filter;

import io.github.gms.common.model.IpRestrictionPattern;
import io.github.gms.functions.iprestriction.IpRestrictionService;
import io.github.gms.functions.iprestriction.IpRestrictionValidator;
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
import java.util.List;

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
    private final IpRestrictionValidator validator;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        List<IpRestrictionPattern> patterns = ipRestrictionService.checkGlobalIpRestrictions();
        boolean ipBlocked = validator.isIpAddressBlocked(patterns);

        if (ipBlocked) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "You are not allowed to get this secret from your IP address!");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
