package io.github.gms.functions.user;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface UserLoginAttemptManagerService {

    void updateLoginAttempt(String username);

    void resetLoginAttempt(String username);

    boolean isBlocked(String username);
}
