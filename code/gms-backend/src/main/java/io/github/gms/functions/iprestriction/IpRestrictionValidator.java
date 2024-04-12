package io.github.gms.functions.iprestriction;

import io.github.gms.common.model.IpRestrictionPattern;
import io.github.gms.common.util.HttpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.gms.common.util.HttpUtils.getClientIpAddress;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IpRestrictionValidator {

    private final HttpServletRequest httpServletRequest;

    public boolean isIpAddressBlocked(List<IpRestrictionPattern> patterns) {
        String ipAddress = getClientIpAddress(httpServletRequest);
        log.info("Client IP address: {}", ipAddress);

        if (patterns.isEmpty()) {
            return false;
        }

        boolean ipIsNotAllowed = patterns.stream().anyMatch(p -> p.isAllow() && !ipAddressMatches(p, ipAddress));
        boolean ipIsBlocked = patterns.stream().anyMatch(p -> !p.isAllow() && ipAddressMatches(p, ipAddress));

        // Returns true if the given IP address is not allowed
        return !HttpUtils.WHITELISTED_ADDRESSES.contains(ipAddress) && (ipIsNotAllowed || ipIsBlocked);
    }

    private static boolean ipAddressMatches(IpRestrictionPattern pattern, String ipAddress) {
        Pattern p = Pattern.compile(pattern.getIpPattern());
        Matcher matcher = p.matcher(ipAddress);
        return matcher.matches();
    }
}