package io.github.gms.functions.secret;

import io.github.gms.abstraction.AbstractAdminRoleSecurityTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.common.dto.BooleanValueDto;
import io.github.gms.functions.secret.dto.SecretDto;
import io.github.gms.functions.secret.dto.SecretListDto;
import io.github.gms.functions.secret.dto.SecretValueDto;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static io.github.gms.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_SECURITY_TEST)
@TestedClass(SecretController.class)
class SecretAdminRoleSecurityTest extends AbstractAdminRoleSecurityTest {

    public SecretAdminRoleSecurityTest() {
        super("/secret");
    }

    @Test
    @TestedMethod(SAVE)
    void save_whenAuthenticationFails_thenReturnHttp403() {
        assertSaveFailWith403(TestUtils.createSaveSecretRequestDto(1L));
    }

    @Test
    @TestedMethod(GET_BY_ID)
    void getById_whenAuthenticationFails_thenReturnHttp403() {
        assertGetByIdFailWith403(SecretDto.class, DemoData.SECRET_ENTITY_ID);
    }

    @Test
    @TestedMethod(LIST)
    void list_whenAuthenticationFails_thenReturnHttp403() {
        assertListFailWith403(SecretListDto.class);
    }

    @Test
    @TestedMethod(GET_VALUE)
    void getValue_whenAuthenticationFails_thenReturnHttp403() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // act
        ResponseEntity<String> response =
                executeHttpGet(urlPrefix + "/value/" + DemoData.SECRET_ENTITY_ID, requestEntity, String.class);

        // assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @TestedMethod(ROTATE_SECRET)
    void rotate_whenAuthenticationFails_thenReturnHttp403() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // act
        ResponseEntity<String> response =
                executeHttpPost(urlPrefix + "/rotate/" + DemoData.SECRET_ENTITY_ID, requestEntity, String.class);

        // assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @TestedMethod(DELETE)
    void delete_whenAuthenticationFails_thenReturnHttp403() {
        assertDeleteFailWith403(DemoData.SECRET_ENTITY2_ID);
    }

    @Test
    @TestedMethod(TOGGLE)
    void toggleStatus_whenAuthenticationFails_thenReturnHttp403() {
        assertToggleFailWith403(DemoData.SECRET_ENTITY_ID);
    }

    @Test
    @TestedMethod("validateValueLength")
    void validateValueLength_whenAuthenticationFails_thenReturnHttp403() {
        // arrange
        SecretValueDto secretValueDto = SecretValueDto.builder()
                .keystoreId(DemoData.KEYSTORE_ID)
                .keystoreAliasId(DemoData.KEYSTORE_ALIAS_ID)
                .secretValues(Map.of("value", "1234567890"))
                .build();
        HttpEntity<SecretValueDto> requestEntity = new HttpEntity<>(secretValueDto, TestUtils.getHttpHeaders(jwt));

        // act
        ResponseEntity<BooleanValueDto> response =
                executeHttpPost(urlPrefix + "/validate_value_length", requestEntity, BooleanValueDto.class);

        // assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
