package io.github.gms.auth.sso.keycloak.converter;

import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.auth.sso.keycloak.model.IntrospectResponse;
import io.github.gms.auth.sso.keycloak.model.RealmAccess;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Clock;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class KeycloakConverterImplTest {

    Clock clock;
    private KeycloakConverterImpl converter;

    @BeforeEach
    void setup() {
        clock = mock(Clock.class);
        converter = new KeycloakConverterImpl(clock);
    }

    @ParameterizedTest
    @MethodSource("toUserDetailsData")
    void shouldReturnUserDetails(String active, EntityStatus expectedStatus) {
        GmsUserDetails userDetails = converter.toUserDetails(IntrospectResponse.builder()
                        .email("email@email")
                        .name("My Name")
                        .username("user1")
                        .active(active)
                        .realmAccess(RealmAccess.builder().roles(List.of("ROLE_USER")).build())
                .build());

        // assert
        assertNotNull(userDetails);
        assertEquals("user1", userDetails.getUsername());
        assertEquals(expectedStatus, userDetails.getStatus());
    }

    @Test
    void shouldReturnUserInfo() {
        // act
        UserInfoDto response = converter.toUserInfoDto(IntrospectResponse.builder()
                .email("email@email")
                .name("My Name")
                .username("user1")
                .realmAccess(RealmAccess.builder().roles(List.of("ROLE_USER")).build())
                .build());

        // assert
        assertNotNull(response);
        assertEquals("email@email", response.getEmail());
        assertEquals("My Name", response.getName());
        assertEquals("user1", response.getUsername());
        assertEquals(UserRole.ROLE_USER, response.getRole());
    }

    private static Object[][] toUserDetailsData() {
        return new Object[][] {
                { "true", EntityStatus.ACTIVE },
                { "false", EntityStatus.DISABLED }
        };
    }
}
