package io.github.gms.functions.iprestriction;

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
@TestedClass(value = IpRestrictionController.class)
class IpRestrictionUserRoleSecurityTest extends AbstractUserRoleSecurityTest {

    public IpRestrictionUserRoleSecurityTest() {
        super("/iprestriction");
    }

    @Test
    @TestedMethod(SAVE)
    void testSaveFailWithHttp403() {
        assertSaveFailWith403(TestUtils.createIpRestrictionDto());
    }

    @Test
    @TestedMethod(LIST)
    void testListFailWithHttp403() {
        assertListFailWith403(IpRestrictionListDto.class);
    }

    @Test
    @TestedMethod(GET_BY_ID)
    void testGetByIdFailWithHttp403() {
        gmsUser = null;
        jwt = null;
        assertGetByIdFailWith403(IpRestrictionDto.class, 1L);
    }

    @Test
    @TestedMethod(DELETE)
    void testDeleteFailWithHttp403() {
        assertDeleteFailWith403(1L);
    }

    @Test
    @TestedMethod(TOGGLE)
    void testToggleStatusFailWithHttp403() {
        assertToggleFailWith403(1L);
    }
}
