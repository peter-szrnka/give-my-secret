package io.github.gms.functions.gdpr;

import io.github.gms.functions.gdpr.model.BatchUserOperationDto;

import java.util.Set;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface UserDeletionService {

    void requestUserDeletion(BatchUserOperationDto dto);

    Set<Long> getRequestedUserDeletionIds();

    void executeRequestedUserDeletion(Set<Long> userIds);
}
