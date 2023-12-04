package io.github.gms.controller.security;

import io.github.gms.abstraction.AbstractUserRoleSecurityTest;
import io.github.gms.secure.dto.ChangePasswordRequestDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.dto.SaveUserRequestDto;
import io.github.gms.secure.dto.UserDto;
import io.github.gms.secure.dto.UserListDto;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class UserRoleSecurityTest extends AbstractUserRoleSecurityTest {

    @Test
    void testSaveFailWithHttp403() {
        gmsUser = null;
        jwt = null;
        HttpEntity<SaveUserRequestDto> requestEntity = new HttpEntity<>(TestUtils.createSaveUserRequestDto(), TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpPost("/secure/user", requestEntity, SaveEntityResponseDto.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testGetByIdFailWithHttp403() {
        gmsUser = null;
        jwt = null;
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpGet("/secure/user/" + DemoData.USER_1_ID, requestEntity, UserDto.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testListFailWithHttp403() {
        PagingDto request = PagingDto.builder().page(0).size(50).direction("ASC").property("id").build();
        HttpEntity<PagingDto> requestEntity = new HttpEntity<>(request, TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpPost("/secure/user/list", requestEntity, UserListDto.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testDeleteFailWithHttp403() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpDelete("/secure/user/" + DemoData.USER_1_ID, requestEntity,
                        String.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testToggleStatusFailWithHttp403() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpPost("/secure/user/" + DemoData.USER_1_ID + "?enabled=true", requestEntity,
                        String.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testChangePasswordFailWithHttp403() {
        gmsUser = null;
        jwt = null;
        ChangePasswordRequestDto request = new ChangePasswordRequestDto();
        HttpEntity<ChangePasswordRequestDto> requestEntity = new HttpEntity<>(request, TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpPost("/secure/user/change_credential", requestEntity,
                        Void.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testGetQrCodeFailWithHttp403() {
        gmsUser = null;
        jwt = null;
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpGet("/secure/user/mfa_qr_code", requestEntity, byte[].class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testToggleMfaFailWithHttp403() {
        gmsUser = null;
        jwt = null;
        HttpEntity<Boolean> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpPost("/secure/user/toggle_mfa?enabled=true", requestEntity,
                        Void.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testIsMfaActiveFailWithHttp403() {
        gmsUser = null;
        jwt = null;
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpGet("/secure/user/mfa_active", requestEntity, Boolean.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }
}
