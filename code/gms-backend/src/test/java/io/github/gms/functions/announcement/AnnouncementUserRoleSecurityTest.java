package io.github.gms.functions.announcement;

import io.github.gms.abstraction.AbstractUserRoleSecurityTest;
import io.github.gms.functions.announcement.SaveAnnouncementDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
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
class AnnouncementUserRoleSecurityTest extends AbstractUserRoleSecurityTest {

    @Test
    void testSaveFailWithHttp403() {
        HttpEntity<SaveAnnouncementDto> requestEntity = new HttpEntity<>(TestUtils.createSaveAnnouncementDto(), TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpPost("/secure/announcement", requestEntity, SaveEntityResponseDto.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }

    @Test
    void testDeleteFailWithHttp403() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // assert
        HttpClientErrorException.Forbidden exception = assertThrows(HttpClientErrorException.Forbidden.class, () ->
                executeHttpDelete("/secure/announcement/" + DemoData.ANNOUNCEMENT_ID, requestEntity,
                        String.class));

        assertTrue(exception.getMessage().startsWith("403"));
    }
}
