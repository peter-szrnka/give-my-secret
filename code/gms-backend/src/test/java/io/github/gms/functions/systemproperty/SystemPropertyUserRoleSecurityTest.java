package io.github.gms.functions.systemproperty;

import io.github.gms.abstraction.AbstractUserRoleSecurityTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.github.gms.util.TestConstants.TAG_SECURITY_TEST;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_SECURITY_TEST)
@TestedClass(SystemPropertyController.class)
class SystemPropertyUserRoleSecurityTest extends AbstractUserRoleSecurityTest {

    public SystemPropertyUserRoleSecurityTest() {
        super("/system_property");
    }

    @Test
    @TestedMethod("save")
    void testSaveFailWithHttp403() {
        shouldSaveFailWith403(TestUtils.createSystemPropertyDto());
    }

    @Test
    @TestedMethod("delete")
    void testDeleteFailWithHttp403() {
        shouldDeleteFailWith403(1L);
    }

    @Test
    @TestedMethod("list")
    void testListFailWithHttp403() {
        shouldListFailWith403(SystemPropertyListDto.class);
    }
}
