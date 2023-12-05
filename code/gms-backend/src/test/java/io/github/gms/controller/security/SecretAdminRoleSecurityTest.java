package io.github.gms.controller.security;

import io.github.gms.abstraction.AbstractAdminRoleSecurityTest;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.dto.SaveSecretRequestDto;
import io.github.gms.secure.dto.SecretDto;
import io.github.gms.secure.dto.SecretListDto;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
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
class SecretAdminRoleSecurityTest extends AbstractAdminRoleSecurityTest {

    @Test
    void testSaveFailWithHttp403() {
        HttpEntity<SaveSecretRequestDto> requestEntity = new HttpEntity<>(TestUtils.createSaveSecretRequestDto(1L), TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpPost("/secure/secret", requestEntity, SaveEntityResponseDto.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testGetByIdFailWithHttp403() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpGet("/secure/secret/" + DemoData.SECRET_ENTITY_ID, requestEntity, SecretDto.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testListFailWithHttp403() {
        PagingDto request = PagingDto.builder().page(0).size(50).direction("ASC").property("id").build();
        HttpEntity<PagingDto> requestEntity = new HttpEntity<>(request, TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpPost("/secure/secret/list", requestEntity, SecretListDto.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testGetValueFailWithHttp403() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpGet("/secure/secret/value/" + DemoData.SECRET_ENTITY_ID, requestEntity, String.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testRotateFailWithHttp403() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpPost("/secure/secret/rotate/" + DemoData.SECRET_ENTITY_ID, requestEntity, String.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testDeleteFailWithHttp403() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpDelete("/secure/secret/" + DemoData.SECRET_ENTITY2_ID, requestEntity,
                        String.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testToggleStatusFailWithHttp403() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpPost("/secure/secret/" + DemoData.SECRET_ENTITY_ID + "?enabled=true", requestEntity,
                        String.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }
}
