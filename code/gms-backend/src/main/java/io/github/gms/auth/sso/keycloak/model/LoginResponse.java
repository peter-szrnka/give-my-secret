package io.github.gms.auth.sso.keycloak.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.gms.common.types.Sensitive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse {

    @Sensitive
    @JsonAlias("access_token")
    private String accessToken;
    @Sensitive
    @JsonAlias("refresh_token")
    private String refreshToken;

    @JsonAlias("error")
    private String error;
    @JsonAlias("error_description")
    private String errorDescription;
}
