package io.github.gms.functions.event;

import io.github.gms.abstraction.AbstractSecurityTest;
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
@TestedClass(EventController.class)
public class EventAdminRoleSecurityTest extends AbstractSecurityTest {

	public EventAdminRoleSecurityTest() {
		super("/event");
	}

	@Test
	@TestedMethod("list")
	public void testListFailWithHttp403() {
		shouldListFailWith403(EventListDto.class);
	}
}