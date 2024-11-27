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
class AnnouncementUserRoleSecurityTest extends AbstractUserRoleSecurityTest {

    public AnnouncementUserRoleSecurityTest() {
        super("/announcement");
    }

    @Test
    @TestedMethod("save")
    void save_whenAuthenticationFails_thenReturnHttp403() {
        shouldSaveFailWith403(TestUtils.createSaveAnnouncementDto());
    }

    @Test
    @TestedMethod("list")
    void list_whenAuthenticationFails_thenReturnHttp403() {
        jwt = null;
        shouldListFailWith403(AnnouncementListDto.class);
    }

    @Test
    @TestedMethod("getById")
    void getById_whenAuthenticationFails_thenReturnHttp403() {
        gmsUser = null;
        jwt = null;
        shouldGetByIdFailWith403(AnnouncementDto.class, DemoData.USER_1_ID);
    }

    @Test
    @TestedMethod("delete")
    void delete_whenAuthenticationFails_thenReturnHttp403() {
        shouldDeleteFailWith403(DemoData.ANNOUNCEMENT_ID);
    }
}
