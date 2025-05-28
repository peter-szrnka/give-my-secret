package io.github.gms.auth;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.functions.system.SystemService;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class VmOptionsPostAuthorizeTest extends AbstractUnitTest {

    @Mock
    private SystemService service;
    @InjectMocks
    private VmOptionsPostAuthorize helper;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    void canAccess_whenSystemIsNotReady_thenAllow() {
        when(service.getSystemStatus()).thenReturn(SystemStatusDto.builder().withStatus("NEED_SETUP").build());

        boolean response = helper.canAccess();

        assertTrue(response);
    }

    @Test
    void canAccess_whenSystemIsReadyButAuthIsNull_thenDeny() {
        when(service.getSystemStatus()).thenReturn(SystemStatusDto.builder().withStatus("OK").build());

        boolean response = helper.canAccess();

        assertFalse(response);
    }

    @Test
    void canAccess_whenSystemIsReadyButUserIsNotAdmin_thenDeny() {
        when(service.getSystemStatus()).thenReturn(SystemStatusDto.builder().withStatus("OK").build());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        TestUtils.createGmsUser(),
                "test",
                        TestUtils.createGmsUser().getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        boolean response = helper.canAccess();

        assertFalse(response);
    }


    @Test
    void canAccess_whenSystemIsReadyButUserIsAdmin_thenAllow() {
        when(service.getSystemStatus()).thenReturn(SystemStatusDto.builder().withStatus("OK").build());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                TestUtils.createGmsAdminUser(),
                "test",
                TestUtils.createGmsAdminUser().getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        boolean response = helper.canAccess();

        assertTrue(response);
    }
}
