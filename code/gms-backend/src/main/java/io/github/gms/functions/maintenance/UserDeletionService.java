package io.github.gms.functions.maintenance;

import io.github.gms.common.abstraction.UserMaintenanceService;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.model.UserMaintenanceConfig;
import io.github.gms.functions.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
public class UserDeletionService extends UserMaintenanceService {

    public UserDeletionService(UserRepository userRepository) {
        super(userRepository, new UserMaintenanceConfig("deletion", EntityStatus.DELETE_REQUESTED));
    }

    @Override
    public void process(Set<Long> userIds) {
        userRepository.deleteAllByUserId(userIds);
    }
}
