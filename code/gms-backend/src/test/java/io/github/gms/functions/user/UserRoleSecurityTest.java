package io.github.gms.functions.user;

import io.github.gms.abstraction.AbstractUserRoleSecurityTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static io.github.gms.util.TestConstants.TAG_SECURITY_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_SECURITY_TEST)
@TestedClass(UserController.class)
class UserRoleSecurityTest extends AbstractUserRoleSecurityTest {

    public UserRoleSecurityTest() {
        super("/user");
    }

    @Test
    @TestedMethod("save")
    void testSaveFailWithHttp403() {
        gmsUser = null;
        jwt = null;
        shouldSaveFailWith403(TestUtils.createSaveUserRequestDto());
    }

    @Test
    @TestedMethod("getById")
    void testGetByIdFailWithHttp403() {
        gmsUser = null;
        jwt = null;
        shouldGetByIdFailWith403(UserDto.class, DemoData.USER_1_ID);
    }

    @Test
    @TestedMethod("list")
    void testListFailWithHttp403() {
        shouldListFailWith403(UserListDto.class);
    }

    @Test
    @TestedMethod("delete")
    void testDeleteFailWithHttp403() {
        shouldDeleteFailWith403(DemoData.USER_1_ID);
    }

    @Test
    @TestedMethod("toggle")
    void testToggleStatusFailWithHttp403() {
        shouldToggleFailWith403(DemoData.USER_1_ID);
    }

    @Test
    @TestedMethod("changePassword")
    void testChangePasswordFailWithHttp403() {
        gmsUser = null;
        jwt = null;
        ChangePasswordRequestDto request = new ChangePasswordRequestDto();
        HttpEntity<ChangePasswordRequestDto> requestEntity = new HttpEntity<>(request, TestUtils.getHttpHeaders(jwt));

        // act
        ResponseEntity<Void> response = executeHttpPost(urlPrefix + "/change_credential", requestEntity, Void.class);

        // assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @TestedMethod("getMfaQrCode")
    void testGetQrCodeFailWithHttp403() {
        gmsUser = null;
        jwt = null;
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(null));

        // act
        ResponseEntity<byte[]> response = executeHttpGet(urlPrefix + "/mfa_qr_code", requestEntity, byte[].class);

        // assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @TestedMethod("toggleMfa")
    void testToggleMfaFailWithHttp403() {
        gmsUser = null;
        jwt = null;
        HttpEntity<Boolean> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // act
        ResponseEntity<Void> response = executeHttpPost(urlPrefix + "/toggle_mfa?enabled=true", requestEntity, Void.class);

        // assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @TestedMethod("isMfaActive")
    void testIsMfaActiveFailWithHttp403() {
        gmsUser = null;
        jwt = null;
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(null));

        // act
        ResponseEntity<String> response = executeHttpGet(urlPrefix + "/mfa_active", requestEntity, String.class);

        // assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
