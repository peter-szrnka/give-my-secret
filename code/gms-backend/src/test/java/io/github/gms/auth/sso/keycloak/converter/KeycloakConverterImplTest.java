package io.github.gms.auth.sso.keycloak.converter;

import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.auth.sso.keycloak.model.IntrospectResponse;
import io.github.gms.auth.sso.keycloak.model.RealmAccess;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.UserRole;
import io.github.gms.functions.user.UserEntity;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static io.github.gms.common.enums.UserRole.ROLE_ADMIN;
import static io.github.gms.common.enums.UserRole.ROLE_USER;
import static io.github.gms.util.TestUtils.USERNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class KeycloakConverterImplTest {

    private static final ZonedDateTime zonedDateTime =
            ZonedDateTime.ofInstant(Instant.parse("2023-06-29T00:00:00Z"), ZoneId.systemDefault());

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

    @Test
    void shouldReturnNewEntity() {
        try (MockedStatic<ZonedDateTime> mockedZonedDateTime = mockStatic(ZonedDateTime.class)) {
            mockedZonedDateTime.when(() -> ZonedDateTime.now(clock)).thenReturn(zonedDateTime);
            // arrange
            UserEntity mockEntity = TestUtils.createAdminUser();
            mockEntity.setId(null);
            UserInfoDto mockUserInfoDto = TestUtils.createUserInfoDto();

            // act
            UserEntity response = converter.toNewEntity(mockEntity, mockUserInfoDto);

            // assert
            assertNotNull(response);
            assertNull(response.getCredential());
            assertNull(response.getId());
            assertNotNull(response.getMfaSecret());
            assertEquals("a@b.com", response.getEmail());
            assertEquals(ROLE_USER, response.getRole());
            assertEquals("name", response.getName());
            assertEquals("user", response.getUsername());
            assertEquals(EntityStatus.ACTIVE, response.getStatus());
            mockedZonedDateTime.verify(() -> ZonedDateTime.now(clock));
        }
    }

    @Test
    void shouldReturnExistingEntity() {
        // arrange
        UserEntity mockEntity = TestUtils.createAdminUser();
        UserInfoDto mockUserInfoDto = TestUtils.createUserInfoDto();

        // act
        UserEntity response = converter.toNewEntity(mockEntity, mockUserInfoDto);

        // assert
        assertNotNull(response);
        assertNotNull(response.getId());
        assertNull(response.getEmail());
        assertEquals(ROLE_ADMIN, response.getRole());
        assertEquals("name", response.getName());
        assertEquals(USERNAME, response.getUsername());
        assertEquals(EntityStatus.ACTIVE, response.getStatus());
    }

    private static Object[][] toUserDetailsData() {
        return new Object[][]{
                {"true", EntityStatus.ACTIVE},
                {"false", EntityStatus.DISABLED}
        };
    }
}
