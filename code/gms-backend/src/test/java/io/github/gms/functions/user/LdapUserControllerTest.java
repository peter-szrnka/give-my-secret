package io.github.gms.functions.user;

import io.github.gms.auth.ldap.LdapSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit test of {@link LdapUserController}
 *
 * @author Peter Szrnka
 */
class LdapUserControllerTest {

    private LdapSyncService ldapSyncService;
    private LdapUserController controller;

    @BeforeEach
    void setupTest() {
        ldapSyncService = mock(LdapSyncService.class);
        controller = new LdapUserController(ldapSyncService, "db");
    }

    @ParameterizedTest
    @MethodSource("inputData")
    void shouldSyncUsers(String authType, int expectedReturnCode) {
        // arrange
        controller = new LdapUserController(ldapSyncService, authType);

        // act
        ResponseEntity<Void> response = controller.synchronizeUsers();

        // assert
        assertNotNull(response);
        assertEquals(expectedReturnCode, response.getStatusCode().value());
        verify(ldapSyncService, times(expectedReturnCode==200 ? 1 : 0)).synchronizeUsers();
    }

    private static Object[][] inputData() {
        return new Object[][] {
                {"db", 404},
                {"ldap", 200}
        };
    }
}
