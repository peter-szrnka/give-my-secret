package io.github.gms.auth.sso.keycloak.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IntrospectResponse {

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
}
