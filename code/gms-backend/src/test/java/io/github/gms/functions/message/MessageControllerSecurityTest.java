package io.github.gms.functions.message;

import io.github.gms.abstraction.AbstractSecurityTest;
import io.github.gms.common.dto.LongValueDto;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static io.github.gms.util.TestConstants.TAG_SECURITY_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_SECURITY_TEST)
class MessageControllerSecurityTest extends AbstractSecurityTest {

    public MessageControllerSecurityTest() {
        super("/message");
    }

    @Test
    void testListFailWithHttp403() {
        shouldListFailWith403(MessageListDto.class);
    }

    @Test
    void testUnreadMessagesCountFailWithHttp403() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // act
        ResponseEntity<LongValueDto> response = executeHttpGet(urlPrefix + "/unread", requestEntity, LongValueDto.class);

        // assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testMarkAsReadFailWithHttp403() {
        MarkAsReadRequestDto request =  MarkAsReadRequestDto.builder().ids(Set.of(1L, 2L, 3L)).build();
        HttpEntity<MarkAsReadRequestDto> requestEntity = new HttpEntity<>(request, TestUtils.getHttpHeaders(jwt));

        // act
        ResponseEntity<String> response = executeHttpPost(urlPrefix + "/mark_as_read", requestEntity, String.class);

        // assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
