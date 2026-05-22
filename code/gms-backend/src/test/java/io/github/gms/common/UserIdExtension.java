package io.github.gms.common;

import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.util.ThreadLocalContext;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class UserIdExtension implements BeforeEachCallback, AfterEachCallback {

    private static final long DEFAULT_USER_ID = 1L;

    @Override
    public void beforeEach(ExtensionContext context) {
        ThreadLocalContext.set(MdcParameter.USER_ID, DEFAULT_USER_ID);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        ThreadLocalContext.remove(MdcParameter.USER_ID);
    }

    public void setDefaultUserId(Long newUserId) {
        ThreadLocalContext.set(MdcParameter.USER_ID, newUserId);
    }
}