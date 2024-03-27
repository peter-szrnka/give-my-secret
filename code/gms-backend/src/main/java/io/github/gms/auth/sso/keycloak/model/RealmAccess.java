package io.github.gms.auth.sso.keycloak.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RealmAccess {

    private List<String> roles;
}
