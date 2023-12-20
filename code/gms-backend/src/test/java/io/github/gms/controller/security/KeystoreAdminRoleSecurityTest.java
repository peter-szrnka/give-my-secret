package io.github.gms.controller.security;

import io.github.gms.abstraction.AbstractAdminRoleSecurityTest;
import io.github.gms.secure.dto.GetSecureValueDto;
import io.github.gms.secure.dto.IdNamePairListDto;
import io.github.gms.secure.dto.KeystoreDto;
import io.github.gms.secure.dto.KeystoreListDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.HttpClientErrorException;

import static io.github.gms.util.TestConstants.TAG_SECURITY_TEST;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_SECURITY_TEST)
class KeystoreAdminRoleSecurityTest extends AbstractAdminRoleSecurityTest {

    @Test
    void testGetByIdFailWithHttp403() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpGet("/secure/keystore/" + DemoData.KEYSTORE_ID, requestEntity, KeystoreDto.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testListFailWithHttp403() {
        PagingDto request = PagingDto.builder().page(0).size(50).direction("ASC").property("id").build();
        HttpEntity<PagingDto> requestEntity = new HttpEntity<>(request, TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpPost("/secure/keystore/list", requestEntity, KeystoreListDto.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testGetValueFailWithHttp403() {
        GetSecureValueDto dto = new GetSecureValueDto();
        dto.setEntityId(DemoData.KEYSTORE_ID);
        HttpEntity<GetSecureValueDto> requestEntity = new HttpEntity<>(dto, TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpPost("/secure/keystore/value", requestEntity, String.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testDeleteFailWithHttp403() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpDelete("/secure/keystore/" + DemoData.KEYSTORE2_ID, requestEntity,
                        String.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testToggleStatusFailWithHttp403() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpPost("/secure/keystore/" + DemoData.KEYSTORE_ID + "?enabled=true", requestEntity,
                        String.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testListAllKeystoreNamesFailWithHttp403() {
        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpGet("/secure/keystore/list_names", null, IdNamePairListDto.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testListAllApiKeyNamesFailWithHttp403() {
        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpGet("/secure/keystore/list_aliases/" + DemoData.KEYSTORE_ID, null, IdNamePairListDto.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testDownloadFailWithHttp403() {
        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpGet("/secure/keystore/download/" + DemoData.KEYSTORE_ID, null, Resource.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }
}
