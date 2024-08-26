package io.github.gms.common.abstraction;

import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.util.CookieUtils;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;

import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static io.github.gms.common.util.Constants.REFRESH_JWT_TOKEN;
import static io.github.gms.common.util.Constants.SET_COOKIE;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RequiredArgsConstructor
public abstract class AbstractLoginController implements GmsController {

    protected final SystemPropertyService systemPropertyService;
    protected final boolean secure;

    protected HttpHeaders addHeaders(AuthenticationResponse authenticateResult) {
        HttpHeaders headers = new HttpHeaders();

        addHeader(headers, ACCESS_JWT_TOKEN, authenticateResult.getToken(), SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
        addHeader(headers, REFRESH_JWT_TOKEN, authenticateResult.getRefreshToken(), SystemProperty.REFRESH_JWT_EXPIRATION_TIME_SECONDS);

        return headers;
    }

    private void addHeader(HttpHeaders headers, String tokenName, String tokenValue, SystemProperty property) {
        if (tokenValue == null) {
            return;
        }

        headers.add(SET_COOKIE, CookieUtils.createCookie(tokenName, tokenValue,
                systemPropertyService.getLong(property), secure).toString());
    }
}
