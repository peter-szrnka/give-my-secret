package io.github.gms.functions.maintenance.job;

import io.github.gms.abstraction.AbstractAdminRoleSecurityTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.github.gms.util.TestConstants.LIST;
import static io.github.gms.util.TestConstants.TAG_SECURITY_TEST;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_SECURITY_TEST)
@TestedClass(JobMaintenanceController.class)
class JobAdminRoleSecurityTest extends AbstractAdminRoleSecurityTest {

    public JobAdminRoleSecurityTest() {
        super("/secure/job");
    }

    @Test
    @TestedMethod(LIST)
    void list_whenAuthenticationFails_thenReturnHttp403() {
        shouldListFailWith403(JobListDto.class);
    }
}
