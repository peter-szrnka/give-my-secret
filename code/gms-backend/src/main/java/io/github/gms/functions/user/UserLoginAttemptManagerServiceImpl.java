package io.github.gms.functions.user;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Profile(CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO)
public class UserLoginAttemptManagerServiceImpl implements UserLoginAttemptManagerService {

    private final UserRepository repository;
    private final SystemPropertyService systemPropertyService;

    @Override
    public void updateLoginAttempt(String username) {
        UserEntity user = getByUsername(username);

        if (user == null) {
            return;
        }

        if (EntityStatus.BLOCKED == user.getStatus()) {
            log.info("User already blocked");
            return;
        }

        Integer attemptsLimit = systemPropertyService.getInteger(SystemProperty.FAILED_ATTEMPTS_LIMIT);
        Integer failedAttempts = user.getFailedAttempts() + 1;
        if (Objects.equals(attemptsLimit, failedAttempts)) {
            user.setStatus(EntityStatus.BLOCKED);
        }

        user.setFailedAttempts(failedAttempts);
        repository.save(user);
    }

    @Override
    public void resetLoginAttempt(String username) {
        UserEntity user = getByUsername(username);

        if (user == null) {
            return;
        }

        user.setFailedAttempts(0);
        repository.save(user);
    }

    @Override
    public boolean isBlocked(String username) {
        UserEntity user = getByUsername(username);
        return user != null && EntityStatus.BLOCKED == user.getStatus();
    }

    private UserEntity getByUsername(String username) {
        return repository.findByUsername(username).orElse(null);
    }
}
