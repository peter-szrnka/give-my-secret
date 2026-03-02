package io.github.gms.common.db.listener;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.model.UserEvent;
import io.github.gms.common.service.GmsThreadLocalValues;
import io.github.gms.common.types.EventSource;
import io.github.gms.functions.apikey.ApiKeyEntity;
import io.github.gms.functions.event.UnprocessedEventStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;

import static io.github.gms.util.LogAssertionUtils.assertLogContains;
import static io.github.gms.util.LogAssertionUtils.assertLogMissing;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class GmsEntityListenerTest extends AbstractLoggingUnitTest {

    @Mock
    private UnprocessedEventStorage unprocessedEventStorage;
    @Mock
    private Clock clock;
    @InjectMocks
    private GmsEntityListener listener;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        addAppender(GmsEntityListener.class);
    }

    @Test
    void beforeAnyUpdate_whenDetailedAuditIsDisabled_thenSkipProcessing() {
        // given
        ReflectionTestUtils.setField(listener, "enableDetailedAudit", false);
        ApiKeyEntity entity = new ApiKeyEntity();
        entity.setId(1L);

        // when
        listener.beforeAnyUpdate(entity);

        // then
        assertLogMissing(logAppender, "[Entity listener] entity will be updated, entityId=1");
    }

    @Test
    void beforeAnyUpdate_whenDetailedAuditEnabled_thenLog() {
        // given
        ReflectionTestUtils.setField(listener, "enableDetailedAudit", true);
        ApiKeyEntity entity = new ApiKeyEntity();
        entity.setId(1L);

        // when
        listener.beforeAnyUpdate(entity);

        // then
        assertLogContains(logAppender, "[Entity listener] entity will be updated, entityId=1");
    }

    @ValueSource(booleans = { true, false })
    @ParameterizedTest
    void afterAnyInsert_whenDetailedAuditIsDisabled_thenSkipProcessing(boolean enableDetailedAudit) {
        // given
        ReflectionTestUtils.setField(listener, "enableDetailedAudit", enableDetailedAudit);
        GmsThreadLocalValues.setEventSource(EventSource.JOB);

        if (enableDetailedAudit) {
            setupClock(clock);
        }

        ApiKeyEntity entity = new ApiKeyEntity();
        listener.afterAnyInsert(entity);

        // then
        if (enableDetailedAudit) {
            ArgumentCaptor<UserEvent> userEventArgumentCaptor = ArgumentCaptor.forClass(UserEvent.class);
            verify(unprocessedEventStorage).addToQueue(userEventArgumentCaptor.capture());

            UserEvent captured = userEventArgumentCaptor.getValue();
            assertEquals(EventOperation.INSERT, captured.getOperation());
            assertEquals(EventTarget.API_KEY, captured.getTarget());
            assertEquals(EventSource.JOB, captured.getEventSource());
        } else {
            verifyNoInteractions(unprocessedEventStorage);
        }

        GmsThreadLocalValues.removeEventSource();
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
