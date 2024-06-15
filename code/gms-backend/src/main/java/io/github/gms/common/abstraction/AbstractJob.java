package io.github.gms.common.abstraction;

import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import lombok.RequiredArgsConstructor;
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
        return systemPropertyService.get(systemProperty) != null &&
                !systemPropertyService.get(systemProperty).equalsIgnoreCase(environment.getProperty("HOSTNAME"));
    }
}
