package io.github.gms.functions.gdpr;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.functions.gdpr.model.BatchUserOperationDto;
import io.github.gms.functions.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDeletionServiceImpl implements UserDeletionService {

    private final UserRepository userRepository;

    @Override
    public void requestUserDeletion(BatchUserOperationDto dto) {
        log.info("Batch user deletion requested. requestId={}", dto.getRequestId());
        userRepository.batchUpdateStatus(dto.getUserIds(), EntityStatus.DELETE_REQUESTED);
    }

    @Override
    public Set<Long> getRequestedUserDeletionIds() {
        return userRepository.findAllByStatus(EntityStatus.DELETE_REQUESTED);
    }

    @Override
    public void executeRequestedUserDeletion(Set<Long> userIds) {
        userRepository.deleteAllByUserId(userIds);
    }
}
