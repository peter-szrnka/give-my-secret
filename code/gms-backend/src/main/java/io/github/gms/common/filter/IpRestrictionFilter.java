package io.github.gms.common.filter;

import io.github.gms.common.model.IpRestrictionPattern;
import io.github.gms.functions.iprestriction.IpRestrictionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A custom Spring filter used to filter incoming API requests by IP address.
 *
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
//@Component
@RequiredArgsConstructor
public class IpRestrictionFilter extends OncePerRequestFilter {

    private final IpRestrictionService ipRestrictionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /*String ipAddress = getClientIpAddress(request);
        log.info("Client IP address: {}", ipAddress);

        // Ip Restriction
        List<IpRestrictionPattern> patterns = ipRestrictionService.getIpRestrictionsBySecret(getSecretId(request));

        boolean ipIsNotAllowed = patterns.stream().filter(IpRestrictionPattern::isAllow).noneMatch(pattern -> ipAddressMatches(pattern, ipAddress));
        boolean ipIsBlocked = patterns.stream().filter(p -> !p.isAllow()).anyMatch(pattern -> ipAddressMatches(pattern, ipAddress));

        if (!HttpUtils.WHITELISTED_ADDRESSES.contains(ipAddress) && (ipIsNotAllowed || ipIsBlocked)) {
            throw new GmsException("You are not allowed to get this secret from your IP address!");
        }*/


        filterChain.doFilter(request, response);
    }

    private Long getSecretId(HttpServletRequest request) {
        return 0L;
    }

    private boolean ipAddressMatches(IpRestrictionPattern pattern, String ipAddress) {
        Pattern p = Pattern.compile(pattern.getIpPattern());
        Matcher matcher = p.matcher(ipAddress);
        return matcher.matches();
    }
}
