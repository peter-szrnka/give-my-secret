package io.github.gms.functions.systemproperty;

import io.github.gms.abstraction.AbstractUserRoleSecurityTest;
import io.github.gms.common.dto.PagingDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.functions.systemproperty.SystemPropertyDto;
import io.github.gms.functions.systemproperty.SystemPropertyListDto;
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
class SystemPropertyUserRoleSecurityTest extends AbstractUserRoleSecurityTest {

    @Test
    void testSaveFailWithHttp403() {
        HttpEntity<SystemPropertyDto> requestEntity = new HttpEntity<>(TestUtils.createSystemPropertyDto(), TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpPost("/secure/system_property", requestEntity, SaveEntityResponseDto.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testDeleteFailWithHttp403() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpDelete("/secure/system_property/1", requestEntity,
                        String.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testListFailWithHttp403() {
        PagingDto request = PagingDto.builder().page(0).size(50).direction("ASC").property("id").build();
        HttpEntity<PagingDto> requestEntity = new HttpEntity<>(request, TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpPost("/secure/system_property/list", requestEntity, SystemPropertyListDto.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }
}
