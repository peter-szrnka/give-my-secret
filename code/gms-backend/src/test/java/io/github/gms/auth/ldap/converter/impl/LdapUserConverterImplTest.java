package io.github.gms.auth.ldap.converter.impl;

import dev.samstevens.totp.secret.SecretGenerator;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class LdapUserConverterImplTest extends AbstractUnitTest {

    private Clock clock;
    private SecretGenerator secretGenerator;
    private LdapUserConverterImpl converter;

    @BeforeEach
    void beforeEach() {
        clock = mock(Clock.class);
        secretGenerator = mock(SecretGenerator.class);
        converter = new LdapUserConverterImpl(clock, secretGenerator);
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void shouldConvertUserDetailsWithNewUser(boolean storeLdapCredential) {
        // arrange
        when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        converter.setStoreLdapCredential(storeLdapCredential);
        GmsUserDetails testUser = TestUtils.createGmsUser();
        when(secretGenerator.generate()).thenReturn("secret!");

        // act
        UserEntity response = converter.toEntity(testUser, null);

        // assert
        assertNotNull(response);
        assertEquals("secret!", response.getMfaSecret());
        assertEquals(storeLdapCredential ? DemoData.CREDENTIAL_TEST : "*PROVIDED_BY_LDAP*", response.getCredential());
        verify(secretGenerator).generate();
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
        when(secretGenerator.generate()).thenReturn("secret!");

        // act
        UserEntity response = converter.toEntity(testUser, existingEntity);

        // assert
        assertNotNull(response);
        assertEquals("secret!", response.getMfaSecret());
        assertEquals(storeLdapCredential ? DemoData.CREDENTIAL_TEST : "*PROVIDED_BY_LDAP*", response.getCredential());
        verify(secretGenerator).generate();
    }
}
