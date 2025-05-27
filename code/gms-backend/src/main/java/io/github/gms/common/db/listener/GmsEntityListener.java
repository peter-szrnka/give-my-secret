package io.github.gms.common.db.listener;

import io.github.gms.common.abstraction.AuditableGmsEntity;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.model.UserEvent;
import io.github.gms.common.service.GmsThreadLocalValues;
import io.github.gms.common.types.EventSource;
import io.github.gms.functions.event.UnprocessedEventStorage;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.ZonedDateTime;

import static java.util.Optional.ofNullable;

@Slf4j
@Component
@RequiredArgsConstructor
public class GmsEntityListener {

    private final UnprocessedEventStorage unprocessedEventStorage;
    private final Clock clock;

    @Value("${config.audit.enableDetailed}")
    private boolean enableDetailedAudit;

    @PrePersist
    @PreUpdate
    public void beforeAnyUpdate(AuditableGmsEntity entity) {
        log.info("[Entity listener] entity will be updated, entityId={}", entity.getId());
    }

    @PostPersist
    public void afterAnyInsert(AuditableGmsEntity entity) {
        handle(entity, EventOperation.INSERT);
    }

    @PostUpdate
    public void afterAnyUpdate(AuditableGmsEntity entity) {
        handle(entity, EventOperation.UPDATE);
    }

    @PostRemove
    public void afterRemove(AuditableGmsEntity entity) {
        handle(entity, EventOperation.DELETE);
    }

    private void handle(AuditableGmsEntity entity, EventOperation eventOperation) {
        if (!enableDetailedAudit) {
            return;
        }

        // Entity target type, like Api key or a secret
        EventTarget target = EventTarget.getByClassName(getClassName(entity));

        // Entity change initiator location
        EventSource eventSource = ofNullable(GmsThreadLocalValues.getEventSource()).orElse(EventSource.UNKNOWN);

        log.info("[Entity listener] userId={}, entityId={}, operation={}, source={}, target={}",
                GmsThreadLocalValues.getUserId(), entity.getId(), eventOperation, eventSource, target);

        unprocessedEventStorage.addToQueue(UserEvent.builder()
                        .userId(GmsThreadLocalValues.getUserId())
                        .entityId(entity.getId())
                        .operation(eventOperation)
                        .eventSource(eventSource)
                        .target(target)
                        .eventDate(ZonedDateTime.now(clock))
                .build());

        GmsThreadLocalValues.removeEventSource();
    }

    private static String getClassName(AuditableGmsEntity entity) {
        return entity.getClass().getSimpleName();
    }
}
