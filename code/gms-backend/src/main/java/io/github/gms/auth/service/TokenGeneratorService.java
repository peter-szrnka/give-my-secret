package io.github.gms.auth.service;

import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.enums.JwtConfigType;

import java.util.Map;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface TokenGeneratorService {

    Map<JwtConfigType, String> getAuthenticationDetails(GmsUserDetails user);
}
