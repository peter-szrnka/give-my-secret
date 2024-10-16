package io.github.gms.common.abstraction;

import io.github.gms.common.model.UserMaintenanceConfig;
import io.github.gms.functions.maintenance.model.BatchUserOperationDto;
import io.github.gms.functions.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
public abstract class UserMaintenanceService {

    protected final UserRepository userRepository;
    private final UserMaintenanceConfig userMaintenanceConfig;

    public void requestProcess(BatchUserOperationDto dto) {
        log.info("Batch user {} requested. requestId={}", userMaintenanceConfig.scope(), dto.getRequestId());
        userRepository.batchUpdateStatus(dto.getUserIds(), userMaintenanceConfig.newStatus());
    }

    public Set<Long> getRequestedUserIds() {
        return userRepository.findAllByStatus(userMaintenanceConfig.newStatus());
    }

    public abstract void process(Set<Long> userIds);
}
