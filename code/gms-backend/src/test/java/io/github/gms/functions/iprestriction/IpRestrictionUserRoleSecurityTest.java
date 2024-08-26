package io.github.gms.functions.iprestriction;

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
@TestedClass(value = IpRestrictionController.class)
class IpRestrictionUserRoleSecurityTest extends AbstractUserRoleSecurityTest {

    public IpRestrictionUserRoleSecurityTest() {
        super("/iprestriction");
    }

    @Test
    @TestedMethod("save")
    void testSaveFailWithHttp403() {
        shouldSaveFailWith403(TestUtils.createIpRestrictionDto());
    }

    @Test
    @TestedMethod("list")
    void testListFailWithHttp403() {
        shouldListFailWith403(IpRestrictionListDto.class);
    }

    @Test
    @TestedMethod("getById")
    void testGetByIdFailWithHttp403() {
        gmsUser = null;
        jwt = null;
        shouldGetByIdFailWith403(IpRestrictionDto.class, 1L);
    }

    @Test
    @TestedMethod("delete")
    void testDeleteFailWithHttp403() {
        shouldDeleteFailWith403(1L);
    }

    @Test
    @TestedMethod("toggle")
    void testToggleStatusFailWithHttp403() {
        shouldToggleFailWith403(1L);
    }
}
