package io.github.gms.functions.gdpr;

import java.util.Set;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface UserAssetDeletionService {

    void executeRequestedUserAssetDeletion(Set<Long> userIds);
}
