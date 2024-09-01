package io.github.gms.functions.message;

import io.github.gms.abstraction.AbstractSecurityTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.common.dto.IdListDto;
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
@TestedClass(MessageController.class)
public class MessageControllerSecurityTest extends AbstractSecurityTest {

    public MessageControllerSecurityTest() {
        super("/message");
    }

    @Test
    @TestedMethod("list")
    void testListFailWithHttp403() {
        shouldListFailWith403(MessageListDto.class);
    }

    @Test
    @TestedMethod("unreadMessagesCount")
    void testUnreadMessagesCountFailWithHttp403() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // act
        ResponseEntity<LongValueDto> response = executeHttpGet(urlPrefix + "/unread", requestEntity, LongValueDto.class);

        // assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @TestedMethod("markAsRead")
    void testMarkAsReadFailWithHttp403() {
        MarkAsReadRequestDto request =  MarkAsReadRequestDto.builder().ids(Set.of(1L, 2L, 3L)).build();
        HttpEntity<MarkAsReadRequestDto> requestEntity = new HttpEntity<>(request, TestUtils.getHttpHeaders(jwt));

        // act
        ResponseEntity<String> response = executeHttpPost(urlPrefix + "/mark_as_read", requestEntity, String.class);

        // assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @TestedMethod("deleteAllByIds")
    void testDeleteAllByIds403() {
        IdListDto request = new IdListDto(Set.of(1L, 2L, 3L));
        HttpEntity<IdListDto> requestEntity = new HttpEntity<>(request, TestUtils.getHttpHeaders(jwt));

        // act
        ResponseEntity<Void> response = executeHttpPost(urlPrefix + "/delete_all_by_ids", requestEntity, Void.class);

        // assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @TestedMethod("deleteById")
    void testDeleteById() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // act
        ResponseEntity<Void> response = executeHttpDelete(urlPrefix + "/1", requestEntity, Void.class);

        // assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
