package io.github.gms.common.abstraction;

import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.core.env.Environment;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RequiredArgsConstructor
public abstract class AbstractJob {

    private final Environment environment;
    protected final SystemPropertyService systemPropertyService;

    protected boolean skipJobExecution(SystemProperty systemProperty) {
        return multiNodeEnabled() && !StringUtils.equals(systemPropertyService.get(systemProperty), environment.getProperty("HOSTNAME"));
    }

    private boolean multiNodeEnabled() {
        return systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE);
    }
}
