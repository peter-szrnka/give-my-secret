package io.github.gms.functions.systemproperty;

import io.github.gms.abstraction.AbstractUserRoleSecurityTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.github.gms.util.TestConstants.*;

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
    @TestedMethod(SAVE)
    void save_whenAuthenticationFails_thenReturnHttp403() {
        assertSaveFailWith403(TestUtils.createSystemPropertyDto());
    }

    @Test
    @TestedMethod(DELETE)
    void delete_whenAuthenticationFails_thenReturnHttp403() {
        assertDeleteFailWith403(1L);
    }

    @Test
    @TestedMethod(LIST)
    void list_whenAuthenticationFails_thenReturnHttp403() {
        assertListFailWith403(SystemPropertyListDto.class);
    }
}
