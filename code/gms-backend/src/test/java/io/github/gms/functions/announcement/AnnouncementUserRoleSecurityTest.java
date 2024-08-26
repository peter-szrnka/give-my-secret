package io.github.gms.functions.announcement;

import io.github.gms.abstraction.AbstractUserRoleSecurityTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.github.gms.util.TestConstants.TAG_SECURITY_TEST;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_SECURITY_TEST)
@TestedClass(AnnouncementController.class)
public class AnnouncementUserRoleSecurityTest extends AbstractUserRoleSecurityTest {

    public AnnouncementUserRoleSecurityTest() {
        super("/announcement");
    }

    @Test
    @TestedMethod("save")
    public void testSaveFailWithHttp403() {
        shouldSaveFailWith403(TestUtils.createSaveAnnouncementDto());
    }

    @Test
    @TestedMethod("delete")
    public void testDeleteFailWithHttp403() {
        shouldDeleteFailWith403(DemoData.ANNOUNCEMENT_ID);
    }
}