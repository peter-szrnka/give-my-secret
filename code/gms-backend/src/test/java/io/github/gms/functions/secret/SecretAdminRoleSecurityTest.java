package io.github.gms.functions.secret;

import io.github.gms.abstraction.AbstractAdminRoleSecurityTest;
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
@TestedClass(SecretController.class)
public class SecretAdminRoleSecurityTest extends AbstractAdminRoleSecurityTest {

    public SecretAdminRoleSecurityTest() {
        super("/secret");
    }

    @Test
    @TestedMethod("save")
    public void testSaveFailWithHttp403() {
        shouldSaveFailWith403(TestUtils.createSaveSecretRequestDto(1L));
    }

    @Test
    @TestedMethod("getById")
    public void testGetByIdFailWithHttp403() {
        shouldGetByIdFailWith403(SecretDto.class, DemoData.SECRET_ENTITY_ID);
    }

    @Test
    @TestedMethod("list")
    public void testListFailWithHttp403() {
        shouldListFailWith403(SecretListDto.class);
    }

    @Test
    @TestedMethod("getValue")
    public void testGetValueFailWithHttp403() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // act
        ResponseEntity<String> response =
                executeHttpGet(urlPrefix + "/value/" + DemoData.SECRET_ENTITY_ID, requestEntity, String.class);

        // assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @TestedMethod("rotateSecret")
    public void testRotateFailWithHttp403() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // act
        ResponseEntity<String> response =
                executeHttpPost(urlPrefix + "/rotate/" + DemoData.SECRET_ENTITY_ID, requestEntity, String.class);

        // assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @TestedMethod("delete")
    public void testDeleteFailWithHttp403() {
        shouldDeleteFailWith403(DemoData.SECRET_ENTITY2_ID);
    }

    @Test
    @TestedMethod("toggle")
    public void testToggleStatusFailWithHttp403() {
        shouldToggleFailWith403(DemoData.SECRET_ENTITY_ID);
    }
}
