package io.github.gms.functions.user;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.common.service.JwtClaimService;
import io.github.gms.common.util.Constants;
import io.github.gms.util.TestUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

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
        addAppender(UserInfoServiceImpl.class);
    }

    @Test
    void getUserInfo_whenJwtCookieIsMissing_thenReturnNull() {
        // arrange
        HttpServletRequest request = mock(HttpServletRequest.class);

        // act
        UserInfoDto response = service.getUserInfo(request);

        // assert
        assertNull(response);
        verify(repository, never()).findById(anyLong());
    }

    @Test
    void getUserInfo_whenUserNotFound_thenReturnNull() {
        // arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("jwt", "value") });
        Claims mockClaims = mock(Claims.class);
        when(mockClaims.get(Constants.USER_ID, Long.class)).thenReturn(1L);
        when(jwtClaimService.getClaims("value")).thenReturn(mockClaims);

        // act
        UserInfoDto response = service.getUserInfo(request);

        // assert
        assertNull(response);
        verify(repository).findById(anyLong());
    }

    @Test
    void getUserInfo_whenInputIsValid_thenReturnOk() {
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
        assertThat(response).hasToString("UserInfoDto(id=1, name=name, username=username, email=a@b.com, role=ROLE_USER, status=null, failedAttempts=null)");
        verify(repository).findById(anyLong());
    }
}
