package io.github.gms.functions.keystore;

import io.github.gms.abstraction.AbstractAdminRoleSecurityTest;
import io.github.gms.functions.secret.GetSecureValueDto;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
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
class KeystoreAdminRoleSecurityTest extends AbstractAdminRoleSecurityTest {

    public KeystoreAdminRoleSecurityTest() {
        super("/keystore");
    }

    @Test
    void testGetByIdFailWithHttp403() {
        shouldGetByIdFailWith403(KeystoreDto.class, DemoData.KEYSTORE_ID);
    }

    @Test
    void testListFailWithHttp403() {
        shouldListFailWith403(KeystoreListDto.class);
    }

    @Test
    void testGetValueFailWithHttp403() {
        GetSecureValueDto dto = new GetSecureValueDto();
        dto.setEntityId(DemoData.KEYSTORE_ID);
        HttpEntity<GetSecureValueDto> requestEntity = new HttpEntity<>(dto, TestUtils.getHttpHeaders(jwt));

        // act
        ResponseEntity<String> response = executeHttpPost("/secure/keystore/value", requestEntity, String.class);

        // assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testDeleteFailWithHttp403() {
        shouldDeleteFailWith403(DemoData.KEYSTORE2_ID);
    }

    @Test
    void testToggleStatusFailWithHttp403() {
        shouldToggleFailWith403(DemoData.KEYSTORE_ID);
    }

    @Test
    void testListAllKeystoreNamesFailWithHttp403() {
        shouldListingFailWith403("/list_names");
    }

    @Test
    void testListAllApiKeyNamesFailWithHttp403() {
        shouldListingFailWith403("/list_aliases/" + DemoData.KEYSTORE_ID);
    }

    @Test
    void testDownloadFailWithHttp403() {
        // act
        ResponseEntity<Resource> response =
                executeHttpGet("/secure/keystore/download/" + DemoData.KEYSTORE_ID, null, Resource.class);

        // assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
