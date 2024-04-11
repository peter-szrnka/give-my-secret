package io.github.gms.common.abstraction;

import java.util.Set;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface BatchDeletionService {

    void batchDeleteByUserIds(Set<Long> userIds);
}
