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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.ZonedDateTime;

import static java.util.Optional.ofNullable;

@Component
@RequiredArgsConstructor
public class GmsEntityListener {

    private static final ThreadLocal<EventOperation> changeTypeThreadLocal = new ThreadLocal<>();

    private final UnprocessedEventStorage unprocessedEventStorage;
    private final Clock clock;
    @Value("${config.audit.enableDetailed}")
    private boolean enableDetailedAudit;

    @PrePersist
    @PreUpdate
    public void beforeAnyUpdate(AuditableGmsEntity entity) {
        changeTypeThreadLocal.set(entity.getId() == null ? EventOperation.INSERT : EventOperation.UPDATE);
    }

    @PostPersist
    public void afterAnyInsert(AuditableGmsEntity entity) {
        handle(entity);
    }

    @PostUpdate
    public void afterAnyUpdate(AuditableGmsEntity entity) {
        handle(entity);
    }

    @PreRemove
    public void beforeRemove(AuditableGmsEntity entity) {
        changeTypeThreadLocal.set(EventOperation.DELETE);
    }

    @PostRemove
    public void afterRemove(AuditableGmsEntity entity) {
        handle(entity);
    }

    private void handle(AuditableGmsEntity entity) {
        if (!enableDetailedAudit) {
            return;
        }

        // Entity target type, like Api key or a secret
        EventTarget target = EventTarget.getByClassName(getClassName(entity));

        // Audit event type, like insert, update, delete or setup the system
        EventOperation eventOperation =
                changeTypeThreadLocal.get() != null ? changeTypeThreadLocal.get() : EventOperation.UNKNOWN;

        // Entity change initiator location
        EventSource eventSource = ofNullable(GmsThreadLocalValues.getEventSource()).orElse(EventSource.UNKNOWN);

        unprocessedEventStorage.addToQueue(UserEvent.builder()
                        .userId(GmsThreadLocalValues.getUserId())
                        .entityId(entity.getId())
                        .operation(eventOperation)
                        .eventSource(eventSource)
                        .target(target)
                        .eventDate(ZonedDateTime.now(clock))
                .build());

        changeTypeThreadLocal.remove();
        GmsThreadLocalValues.removeEventSource();
    }

    private static String getClassName(AuditableGmsEntity entity) {
        return entity.getClass().getSimpleName();
    }
}
