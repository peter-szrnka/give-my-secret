package io.github.gms.functions.iprestriction;

import io.github.gms.abstraction.AbstractUserRoleSecurityTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.github.gms.util.TestConstants.TAG_SECURITY_TEST;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_SECURITY_TEST)
@TestedClass(value = IpRestrictionController.class)
public class IpRestrictionUserRoleSecurityTest extends AbstractUserRoleSecurityTest {

    public IpRestrictionUserRoleSecurityTest() {
        super("/iprestriction");
    }

    @Test
    @TestedMethod("list")
    public void testListFailWithHttp403() {
        shouldListFailWith403(IpRestrictionListDto.class);
    }
}
