package io.github.gms.auth.service;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.converter.GenerateJwtRequestConverter;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.UserRole;
import io.github.gms.common.model.GenerateJwtRequest;
import io.github.gms.common.service.JwtService;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class TokenGeneratorServiceTest extends AbstractUnitTest {

    private JwtService jwtService;
    private GenerateJwtRequestConverter generateJwtRequestConverter;
    private TokenGeneratorService service;

    @BeforeEach
    public void setUp() {
        jwtService = mock(JwtService.class);
        generateJwtRequestConverter = mock(GenerateJwtRequestConverter.class);
        service = new TokenGeneratorService(jwtService, generateJwtRequestConverter);
    }

    @Test
    void getAuthenticationDetails_whenAllConditionsMet_thenProvideMap() {
        // arrange
        GmsUserDetails user = TestUtils.createGmsUser();
        when(generateJwtRequestConverter.toRequest(eq(JwtConfigType.REFRESH_JWT), eq(user.getUsername()), any())).thenReturn(TestUtils.createJwtUserRequest(user));
        when(generateJwtRequestConverter.toRequest(eq(JwtConfigType.ACCESS_JWT), eq(user.getUsername()), any())).thenReturn(TestUtils.createJwtUserRequest(user));
        when(jwtService.generateJwts(any())).thenReturn(Map.of(JwtConfigType.ACCESS_JWT, "access_token", JwtConfigType.REFRESH_JWT, "refresh_token"));

        // act
        Map<JwtConfigType, String> response = service.getAuthenticationDetails(user);

        // assert
        assertNotNull(response);
        assertThat(response)
                .containsEntry(JwtConfigType.ACCESS_JWT, "access_token")
                .containsEntry(JwtConfigType.REFRESH_JWT, "refresh_token");

        ArgumentCaptor<Map<String, Object>> refreshClaimsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(generateJwtRequestConverter).toRequest(eq(JwtConfigType.REFRESH_JWT), eq(user.getUsername()), refreshClaimsCaptor.capture());
        assertThat(refreshClaimsCaptor.getValue()).containsEntry(MdcParameter.USER_NAME.getDisplayName(), user.getUsername());

        ArgumentCaptor<Map<String, Object>> accessClaimsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(generateJwtRequestConverter).toRequest(eq(JwtConfigType.ACCESS_JWT), eq(user.getUsername()), accessClaimsCaptor.capture());
        assertThat(accessClaimsCaptor.getValue()).containsEntry(MdcParameter.USER_ID.getDisplayName(), user.getUserId());
        assertThat(accessClaimsCaptor.getValue()).containsEntry(MdcParameter.USER_NAME.getDisplayName(), user.getUsername());

        Set<UserRole> capturedRoles = (Set<UserRole>) accessClaimsCaptor.getValue().get("roles");
        assertEquals(UserRole.ROLE_USER, capturedRoles.iterator().next());

        ArgumentCaptor<Map<JwtConfigType, GenerateJwtRequest>> jwtServiceCaptor = ArgumentCaptor.forClass(Map.class);
        verify(jwtService).generateJwts(jwtServiceCaptor.capture());

        assertThat(jwtServiceCaptor.getValue().get(JwtConfigType.ACCESS_JWT).getClaims()).containsEntry(MdcParameter.USER_ID.getDisplayName(), user.getUserId());
        assertThat(jwtServiceCaptor.getValue().get(JwtConfigType.ACCESS_JWT).getClaims()).containsEntry(MdcParameter.USER_NAME.getDisplayName(), user.getUsername());
        assertThat(jwtServiceCaptor.getValue().get(JwtConfigType.REFRESH_JWT).getClaims()).containsEntry(MdcParameter.USER_NAME.getDisplayName(), user.getUsername());
    }
}
