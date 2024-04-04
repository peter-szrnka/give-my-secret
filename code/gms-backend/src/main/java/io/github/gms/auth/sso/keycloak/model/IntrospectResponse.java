package io.github.gms.auth.sso.keycloak.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IntrospectResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 6099923075106178108L;

    @JsonAlias("username")
    private String username;
    @JsonAlias("name")
    private String name;
    @JsonAlias("email")
    private String email;
    @JsonAlias("active")
    private String active;
    @JsonAlias("realm_access")
    private RealmAccess realmAccess;
    @JsonAlias("failed_attempts")
    private Integer failedAttempts;

    @JsonAlias("error")
    private String error;
    @JsonAlias("error_description")
    private String errorDescription;
}
