package io.github.gms.functions.announcement;

import io.github.gms.abstraction.AbstractUserRoleSecurityTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.github.gms.util.TestConstants.*;

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
    @TestedMethod(SAVE)
    void save_whenAuthenticationFails_thenReturnHttp403() {
        assertSaveFailWith403(TestUtils.createSaveAnnouncementDto());
    }

    @Test
    @TestedMethod(LIST)
    void list_whenAuthenticationFails_thenReturnHttp403() {
        jwt = null;
        assertListFailWith403(AnnouncementListDto.class);
    }

    @Test
    @TestedMethod(GET_BY_ID)
    void getById_whenAuthenticationFails_thenReturnHttp403() {
        gmsUser = null;
        jwt = null;
        assertGetByIdFailWith403(AnnouncementDto.class, DemoData.USER_1_ID);
    }

    @Test
    @TestedMethod(DELETE)
    void delete_whenAuthenticationFails_thenReturnHttp403() {
        assertDeleteFailWith403(DemoData.ANNOUNCEMENT_ID);
    }
}
