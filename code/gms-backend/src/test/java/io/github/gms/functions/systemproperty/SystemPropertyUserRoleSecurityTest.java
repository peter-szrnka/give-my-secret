package io.github.gms.functions.systemproperty;

import io.github.gms.abstraction.AbstractUserRoleSecurityTest;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.github.gms.util.TestConstants.TAG_SECURITY_TEST;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_SECURITY_TEST)
class SystemPropertyUserRoleSecurityTest extends AbstractUserRoleSecurityTest {

    public SystemPropertyUserRoleSecurityTest() {
        super("/system_property");
    }

    @Test
    void testSaveFailWithHttp403() {
        shouldSaveFailWith403(TestUtils.createSystemPropertyDto());
    }

    @Test
    void testDeleteFailWithHttp403() {
        shouldDeleteFailWith403(1L);
    }

    @Test
    void testListFailWithHttp403() {
        shouldListFailWith403(SystemPropertyListDto.class);
    }
}
