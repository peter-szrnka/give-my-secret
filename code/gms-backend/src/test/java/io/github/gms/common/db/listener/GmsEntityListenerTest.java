package io.github.gms.common.db.listener;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.functions.apikey.ApiKeyEntity;
import io.github.gms.functions.event.UnprocessedEventStorage;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;

import static org.mockito.Mockito.verifyNoInteractions;

class GmsEntityListenerTest extends AbstractUnitTest {

    @Mock
    private UnprocessedEventStorage unprocessedEventStorage;
    @Mock
    private Clock clock;
    @InjectMocks
    private GmsEntityListener listener;

    @Test
    void afterAnyInsert_whenDetailedAuditIsDisabled_thenSkipProcessing() {
        // given
        ReflectionTestUtils.setField(listener, "enableDetailedAudit", false);

        listener.afterAnyInsert(new ApiKeyEntity());

        // then
        verifyNoInteractions(unprocessedEventStorage);
    }

    @Test
    void afterAnyUpdate_whenDetailedAuditIsDisabled_thenSkipProcessing() {
        // given
        ReflectionTestUtils.setField(listener, "enableDetailedAudit", false);

        listener.afterAnyUpdate(new ApiKeyEntity());

        // then
        verifyNoInteractions(unprocessedEventStorage);
    }

    @Test
    void afterRemove_whenDetailedAuditIsDisabled_thenSkipProcessing() {
        // given
        ReflectionTestUtils.setField(listener, "enableDetailedAudit", false);

        listener.afterRemove(new ApiKeyEntity());

        // then
        verifyNoInteractions(unprocessedEventStorage);
    }
}
