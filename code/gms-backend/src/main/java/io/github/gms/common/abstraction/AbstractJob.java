package io.github.gms.common.abstraction;

import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.StringUtils;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RequiredArgsConstructor
public abstract class AbstractJob {

    private final SystemService systemService;
    protected final SystemPropertyService systemPropertyService;
    private final SystemProperty enableProperty;

    protected boolean skipJobExecution(SystemProperty systemProperty) {
        return jobDisabled() || multiNodeEnabled(systemProperty);
    }

    private boolean jobDisabled() {
        return !systemPropertyService.getBoolean(enableProperty);
    }

    private boolean multiNodeEnabled(SystemProperty systemProperty) {
        return multiNodeEnabled() && !StringUtils.equals(systemPropertyService.get(systemProperty), systemService.getContainerId());
    }

    private boolean multiNodeEnabled() {
        return systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE);
    }
}
