package io.github.gms.auth.ldap.converter.impl;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.functions.user.UserEntity;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class LdapUserConverterImplTest extends AbstractUnitTest {

    private Clock clock;
    private LdapUserConverterImpl converter;

    @BeforeEach
    void beforeEach() {
        clock = mock(Clock.class);
        converter = new LdapUserConverterImpl(clock);
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void shouldConvertUserDetailsWithNewUser(boolean storeLdapCredential) {
        // arrange
        when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        converter.setStoreLdapCredential(storeLdapCredential);
        GmsUserDetails testUser = TestUtils.createGmsUser();

        // act
        UserEntity response = converter.toEntity(testUser, null);

        // assert
        assertNotNull(response);
        assertEquals(storeLdapCredential ? DemoData.CREDENTIAL_TEST : "*PROVIDED_BY_LDAP*", response.getCredential());
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void shouldConvertUserDetailsWithExistingUser(boolean storeLdapCredential) {
        // arrange
        when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        converter.setStoreLdapCredential(storeLdapCredential);
        GmsUserDetails testUser = TestUtils.createGmsUser();
        UserEntity existingEntity = TestUtils.createUser();

        // act
        UserEntity response = converter.toEntity(testUser, existingEntity);

        // assert
        assertNotNull(response);
        assertEquals(storeLdapCredential ? DemoData.CREDENTIAL_TEST : "*PROVIDED_BY_LDAP*", response.getCredential());
    }
}
