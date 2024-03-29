package io.github.gms.functions.user;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.common.service.JwtClaimService;
import io.github.gms.common.types.GmsException;
import io.github.gms.common.util.Constants;
import io.github.gms.util.TestUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class UserInfoServiceImplTest extends AbstractLoggingUnitTest {

    private UserRepository repository;
    private JwtClaimService jwtClaimService;
    private UserInfoServiceImpl service;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        repository = mock(UserRepository.class);
        jwtClaimService = mock(JwtClaimService.class);
        service = new UserInfoServiceImpl(repository, jwtClaimService);
        ((Logger) LoggerFactory.getLogger(UserInfoServiceImpl.class)).addAppender(logAppender);
    }

    @Test
    void jwtCookieIsMissing() {
        // arrange
        HttpServletRequest request = mock(HttpServletRequest.class);

        // act
        UserInfoDto response = service.getUserInfo(request);

        // assert
        assertNull(response);
        verify(repository, never()).findById(anyLong());
    }

    @Test
    void userNotFound() {
        // arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("jwt", "value") });
        Claims mockClaims = mock(Claims.class);
        when(mockClaims.get(Constants.USER_ID, Long.class)).thenReturn(1L);
        when(jwtClaimService.getClaims("value")).thenReturn(mockClaims);

        // act
        GmsException exception = assertThrows(GmsException.class, () -> service.getUserInfo(request));

        // assert
        assertNotNull(exception);
        assertEquals("User not found!", exception.getMessage());
        verify(repository).findById(anyLong());
    }

    @Test
    void shouldReturnUserInfo() {
        // arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("jwt", "value") });
        Claims mockClaims = mock(Claims.class);
        when(mockClaims.get(Constants.USER_ID, Long.class)).thenReturn(1L);
        when(jwtClaimService.getClaims("value")).thenReturn(mockClaims);
        when(repository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createUser()));

        // act
        UserInfoDto response = service.getUserInfo(request);

        // assert
        assertNotNull(response);
        verify(repository).findById(anyLong());
    }
}
